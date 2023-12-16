package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.response.ListResponse;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.FriendRequest;
import com.example.facebookbackend.entities.Friendship;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.repositories.FriendRequestRepository;
import com.example.facebookbackend.repositories.FriendshipRepository;
import com.example.facebookbackend.repositories.UserRepository;
import com.example.facebookbackend.services.FriendServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    @Override
    public ResponseEntity<MessageResponse> createAddFriendRequest(String sentEmail, AddFriendRequest addFriendRequest) {
        Optional<User> sender = userRepository.findByEmail(sentEmail);
        Optional<User> receiver = userRepository.findByEmail(addFriendRequest.getToUserEmail());
        if (sender.isEmpty() || !sender.get().isEnable()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Không tồn tại người dùng có email: " + sentEmail));
        } else if (receiver.isEmpty() || !receiver.get().isEnable()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Không tồn tại người dùng có email: " + addFriendRequest.getToUserEmail()));
        } else if (sender.equals(receiver)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Không thể tự kết bạn với chính mình"));
        } else {
            List<FriendRequest> receivedRequestList = friendRequestRepository.findFriendRequestsByReceiver(sender.get());
            for (FriendRequest req : receivedRequestList) {
                if (req.getSender().equals(receiver.get())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Người này đã gửi lời mời kết bạn đến bạn. Hãy đồng ý"));
                }
            }
            List<FriendRequest> sentRequestList = friendRequestRepository.findFriendRequestsBySender(sender.get());
            for (FriendRequest req : sentRequestList) {
                if (req.getReceiver().equals(receiver.get())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Bạn đã gửi lời mời kết bạn đến người này. Hãy chờ họ đồng ý"));
                }
            }
            if ((friendshipRepository.findByUser1AndUser2(sender.get(), receiver.get()) != null)
                    || friendshipRepository.findByUser1AndUser2(receiver.get(), receiver.get()) != null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Bạn và người này đã là bạn bè"));
            }
            if (addFriendRequest.getMessage() == null || addFriendRequest.getMessage().isEmpty()) {
                addFriendRequest.setMessage("Xin chào, tôi là " + sender.get().getFullName() + ". Chúng ta kết bạn nhé!");
            }
            FriendRequest friendRequest = new FriendRequest(sender.get(),
                    receiver.get(),
                    addFriendRequest.getMessage(),
                    LocalDateTime.now());
            friendRequestRepository.save(friendRequest);
            return ResponseEntity.ok().body(new MessageResponse("Gửi yêu cầu kết bạn thành công. Hãy chờ người ấy xác nhận"));
        }
    }

    @Override
    public ListResponse getSentAddFriendRequestList(String email, Integer page, Integer size) {
        ListResponse listResponse = new ListResponse();
        Optional<User> sentUser = userRepository.findByEmail(email);
        if (sentUser.isEmpty() || !sentUser.get().isEnable()) {
            listResponse.setListResult(null);
            listResponse.setMessage("Không tồn tại người dùng có email: " + email);
        } else {
            List<FriendRequest> friendRequestList = friendRequestRepository.findFriendRequestsBySender(sentUser.get());
            listResponse.setListResult(ListResponse.pagingList(friendRequestList, page, size));
            listResponse.setMessage("Trả về " + listResponse.getListResult().size() + " kết quả");
        }
        return listResponse;
    }

    @Override
    public ListResponse getReceivedAddFriendRequestList(String email, Integer page, Integer size) {
        ListResponse listResponse = new ListResponse();
        Optional<User> receivedUser = userRepository.findByEmail(email);
        if (receivedUser.isEmpty() || !receivedUser.get().isEnable()) {
            listResponse.setListResult(null);
            listResponse.setMessage("Không tồn tại người dùng có email: " + email);
        } else {
            List<FriendRequest> friendRequestList = friendRequestRepository.findFriendRequestsByReceiver(receivedUser.get());
            listResponse.setListResult(ListResponse.pagingList(friendRequestList, page, size));
            listResponse.setMessage("Trả về " + listResponse.getListResult().size() + " kết quả");
        }
        return listResponse;

    }

    @Override
    public ResponseEntity<?> respondFriendRequest(String email, int id, Boolean isAccept) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findById(id);
        if (friendRequest.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Không tồn tại request có id: " + id));
        } else {
            Optional<User> receivedUser = userRepository.findByEmail(email);
            if (receivedUser.isEmpty() || !receivedUser.get().isEnable()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Không tồn tại người dùng có email: " + email));
            } else {
                if (friendRequest.get().getReceiver().equals(receivedUser.get())) {
                    if (isAccept != null) {
                        if (isAccept) {
                            //Thêm Friendship
                            Friendship friendship = new Friendship(friendRequest.get().getSender(), friendRequest.get().getReceiver(), LocalDateTime.now());
                            friendshipRepository.save(friendship);
                            friendRequestRepository.delete(friendRequest.get());
                            return ResponseEntity.ok().body(new MessageResponse("Bạn và " + friendRequest.get().getSender().getFullName() + " đã trở thành bạn bè"));
                        }
                        //Xóa cứng FriendReq
                        friendRequestRepository.delete(friendRequest.get());
                        return ResponseEntity.ok().body(new MessageResponse("Bạn đã từ chối lời mời kết bạn từ " + friendRequest.get().getSender().getFullName()));
                    }
                    return ResponseEntity.ok().body(friendRequest.get());
                } else {
                    return ResponseEntity.ok().body(new MessageResponse("Bạn không có lời mời kết bạn này"));
                }
            }
        }
    }

    @Override
    public ListResponse getUserFriendList(String email, Integer page, Integer size) {
        ListResponse listResponse = new ListResponse();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() || !user.get().isEnable()) {
            listResponse.setListResult(null);
            listResponse.setMessage("Không tồn tại người dùng có email: " + email);
        } else {
            List<Friendship> friendshipList = new ArrayList<>();
            friendshipList.addAll(friendshipRepository.findByUser1(user.get()));
            friendshipList.addAll(friendshipRepository.findByUser2(user.get()));
            listResponse.setListResult(ListResponse.pagingList(friendshipList, size, page));
            listResponse.setMessage("Trả về " + listResponse.getListResult().size() + " kết quả");
        }
        return listResponse;
    }
}