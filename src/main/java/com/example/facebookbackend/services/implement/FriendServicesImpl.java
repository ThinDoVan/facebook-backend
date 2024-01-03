package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.response.FriendRequestDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.UserDto;
import com.example.facebookbackend.entities.FriendRequest;
import com.example.facebookbackend.entities.Friendship;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.exceptions.DataNotFoundException;
import com.example.facebookbackend.exceptions.InvalidDataException;
import com.example.facebookbackend.repositories.FriendRequestRepository;
import com.example.facebookbackend.repositories.FriendshipRepository;
import com.example.facebookbackend.repositories.UserRepository;
import com.example.facebookbackend.services.FriendServices;
import com.example.facebookbackend.utils.AccessControlUtils;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendServicesImpl implements FriendServices {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FriendRequestRepository friendRequestRepository;
    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    AccessControlUtils accessControlUtils;
    @Autowired
    ResponseUtils responseUtils;

    public MessageResponse createAddFriendRequest(User sender, AddFriendRequest addFriendRequest) {
        Optional<User> receiver = userRepository.findByEmail(addFriendRequest.getToUserEmail());
        if (receiver.isEmpty() || !receiver.get().isEnable()) {
            throw new DataNotFoundException("Không tìm thấy người dùng có email: " + addFriendRequest.getToUserEmail());
        } else if (sender.equals(receiver.get())) {
            throw new InvalidDataException("Không thể tự kết bạn với chính mình");
        } else {
            List<FriendRequest> receivedRequestList = friendRequestRepository.findFriendRequestsByReceiver(sender);
            for (FriendRequest req : receivedRequestList) {
                if (req.getSender().equals(receiver.get())) {
                    throw new InvalidDataException("Người này đã gửi lời mời kết bạn đến bạn. Hãy đồng ý");
                }
            }
            List<FriendRequest> sentRequestList = friendRequestRepository.findFriendRequestsBySender(sender);
            for (FriendRequest req : sentRequestList) {
                if (req.getReceiver().equals(receiver.get())) {
                    throw new InvalidDataException("Bạn đã gửi lời mời kết bạn đến người này. Hãy chờ họ đồng ý");
                }
            }
            if (accessControlUtils.isFriend(receiver.get(), sender)) {
                throw new InvalidDataException("Bạn và người này đã là bạn bè");
            }
            if (addFriendRequest.getMessage() == null || addFriendRequest.getMessage().isEmpty()) {
                addFriendRequest.setMessage("Xin chào, tôi là " + sender.getFullName() + ". Chúng ta kết bạn nhé!");
            }
            FriendRequest friendRequest = new FriendRequest(sender,
                    receiver.get(),
                    addFriendRequest.getMessage(),
                    LocalDateTime.now());
            friendRequestRepository.save(friendRequest);
            return new MessageResponse("Gửi yêu cầu kết bạn thành công. Hãy chờ người ấy xác nhận");
        }
    }

    @Override
    public Page<FriendRequestDto> getSentAddFriendRequestList(User currentUser, Integer page, Integer size) {

        List<FriendRequestDto> friendRequestDtoList = new ArrayList<>();
        for (FriendRequest result : friendRequestRepository.findFriendRequestsBySender(currentUser)) {
            friendRequestDtoList.add(responseUtils.getFriendRequestInfo(result));
        }
        return responseUtils.pagingList(friendRequestDtoList, page, size);
    }

    @Override
    public Page<FriendRequestDto> getReceivedAddFriendRequestList(User currentUser, Integer page, Integer size) {

        List<FriendRequestDto> friendRequestDtoList = new ArrayList<>();
        for (FriendRequest result : friendRequestRepository.findFriendRequestsByReceiver(currentUser)) {
            friendRequestDtoList.add(responseUtils.getFriendRequestInfo(result));
        }
        return responseUtils.pagingList(friendRequestDtoList, page, size);
    }

    @Override
    public FriendRequestDto getFriendRequest(User currentUser, int id) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findById(id);
        if (friendRequest.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy request có id: " + id);
        } else {
            return responseUtils.getFriendRequestInfo(friendRequest.get());
        }
    }


    @Override
    public MessageResponse respondFriendRequest(User currentUser, int id, boolean isAccept) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findById(id);
        if (friendRequest.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy request có id: " + id);
        } else {
            if (friendRequest.get().getReceiver().equals(currentUser)) {

                if (isAccept) {
                    //Thêm Friendship
                    Friendship friendship = new Friendship(friendRequest.get().getSender(), friendRequest.get().getReceiver(), LocalDateTime.now());
                    friendshipRepository.save(friendship);
                    friendRequestRepository.delete(friendRequest.get());
                    return new MessageResponse("Bạn và " + friendRequest.get().getSender().getFullName() + " đã trở thành bạn bè");
                } else {
                    //Xóa cứng FriendReq
                    friendRequestRepository.delete(friendRequest.get());
                    return new MessageResponse("Bạn đã từ chối lời mời kết bạn từ " + friendRequest.get().getSender().getFullName());

                }
            } else {
                throw new DataNotFoundException("Bạn không có lời mời kết bạn này");
            }
        }
    }

    @Override
    public Page<UserDto> getUserFriendList(User currentUser, Integer page, Integer size) {
        List<UserDto> friendsList = new ArrayList<>();
        for (User result : getFriendList(currentUser)) {
            friendsList.add(responseUtils.getUserInfo(result));
        }
        return responseUtils.pagingList(friendsList, page, size);
    }

    private List<User> getFriendList(User user) {
        List<User> friendsList = new ArrayList<>();
        for (Friendship result : friendshipRepository.findByUser1(user)) {
            friendsList.add(result.getUser2());
        }
        for (Friendship result : friendshipRepository.findByUser2(user)) {
            friendsList.add(result.getUser1());
        }
        return friendsList;
    }
}
