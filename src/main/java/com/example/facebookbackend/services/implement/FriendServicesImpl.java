package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.response.FriendRequestDto;
import com.example.facebookbackend.dtos.response.FriendshipDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.FriendRequest;
import com.example.facebookbackend.entities.Friendship;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.helper.ResponseUtils;
import com.example.facebookbackend.repositories.FriendRequestRepository;
import com.example.facebookbackend.repositories.FriendshipRepository;
import com.example.facebookbackend.repositories.UserRepository;
import com.example.facebookbackend.services.FriendServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendServicesImpl implements FriendServices {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FriendRequestRepository friendRequestRepository;
    @Autowired
    FriendshipRepository friendshipRepository;


    @Override
    public ResponseEntity<MessageResponse> createAddFriendRequest(User sender, AddFriendRequest addFriendRequest) {

        Optional<User> receiver = userRepository.findByEmail(addFriendRequest.getToUserEmail());
        if (receiver.isEmpty() || !receiver.get().isEnable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có email: " + addFriendRequest.getToUserEmail()));
        } else if (sender.equals(receiver.get())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Không thể tự kết bạn với chính mình"));
        } else {
            List<FriendRequest> receivedRequestList = friendRequestRepository.findFriendRequestsByReceiver(sender);
            for (FriendRequest req : receivedRequestList) {
                if (req.getSender().equals(receiver.get())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Người này đã gửi lời mời kết bạn đến bạn. Hãy đồng ý"));
                }
            }
            List<FriendRequest> sentRequestList = friendRequestRepository.findFriendRequestsBySender(sender);
            for (FriendRequest req : sentRequestList) {
                if (req.getReceiver().equals(receiver.get())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Bạn đã gửi lời mời kết bạn đến người này. Hãy chờ họ đồng ý"));
                }
            }
            if ((friendshipRepository.findByUser1AndUser2(sender, receiver.get()) != null)
                    || friendshipRepository.findByUser1AndUser2(receiver.get(), receiver.get()) != null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Bạn và người này đã là bạn bè"));
            }
            if (addFriendRequest.getMessage() == null || addFriendRequest.getMessage().isEmpty()) {
                addFriendRequest.setMessage("Xin chào, tôi là " + sender.getFullName() + ". Chúng ta kết bạn nhé!");
            }
            FriendRequest friendRequest = new FriendRequest(sender,
                    receiver.get(),
                    addFriendRequest.getMessage(),
                    LocalDateTime.now());
            friendRequestRepository.save(friendRequest);
            return ResponseEntity.ok().body(new MessageResponse("Gửi yêu cầu kết bạn thành công. Hãy chờ người ấy xác nhận"));
        }
    }

    @Override
    public ResponseEntity<?> getSentAddFriendRequestList(User currentUser, Integer page, Integer size) {

            List<FriendRequestDto> friendRequestDtoList = new ArrayList<>();
            for (FriendRequest result : friendRequestRepository.findFriendRequestsBySender(currentUser)) {
                friendRequestDtoList.add(modelMapper.map(result, FriendRequestDto.class));
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.pagingList(friendRequestDtoList, page, size));
        }

    @Override
    public ResponseEntity<?> getReceivedAddFriendRequestList(User currentUser, Integer page, Integer size) {

            List<FriendRequestDto> friendRequestDtoList = new ArrayList<>();
            for (FriendRequest result : friendRequestRepository.findFriendRequestsByReceiver(currentUser)) {
                friendRequestDtoList.add(modelMapper.map(result, FriendRequestDto.class));
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.pagingList(friendRequestDtoList, page, size));
        }


    @Override
    public ResponseEntity<?> respondFriendRequest(User currentUser, int id, Boolean isAccept) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findById(id);
        if (friendRequest.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy request có id: " + id));
        } else {
                if (friendRequest.get().getReceiver().equals(currentUser)) {
                    if (isAccept != null) {
                        if (isAccept) {
                            //Thêm Friendship
                            Friendship friendship = new Friendship(friendRequest.get().getSender(), friendRequest.get().getReceiver(), LocalDateTime.now());
                            friendshipRepository.save(friendship);
                            friendRequestRepository.delete(friendRequest.get());
                            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Bạn và " + friendRequest.get().getSender().getFullName() + " đã trở thành bạn bè"));
                        }
                        //Xóa cứng FriendReq
                        friendRequestRepository.delete(friendRequest.get());
                        return ResponseEntity.ok().body(new MessageResponse("Bạn đã từ chối lời mời kết bạn từ " + friendRequest.get().getSender().getFullName()));
                    }else {
                        return ResponseEntity.ok().body(modelMapper.map(friendRequest.get(),FriendRequestDto.class));
                    }
                } else {
                    return ResponseEntity.ok().body(new MessageResponse("Bạn không có lời mời kết bạn này"));
                }
            }
        }


    @Override
    public ResponseEntity<?> getUserFriendList(User currentUser, Integer page, Integer size) {

            List<FriendshipDto> friendRequestDtoList = new ArrayList<>();
            for (Friendship result : friendshipRepository.findByUser1(currentUser)) {
                friendRequestDtoList.add(modelMapper.map(result, FriendshipDto.class));
            }
            for (Friendship result : friendshipRepository.findByUser2(currentUser)) {
                friendRequestDtoList.add(modelMapper.map(result, FriendshipDto.class));
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.pagingList(friendRequestDtoList, page, size));
        }
    }
