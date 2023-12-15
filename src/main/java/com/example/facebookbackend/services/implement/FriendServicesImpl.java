package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.FriendRequest;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.repositories.FriendRequestRepository;
import com.example.facebookbackend.repositories.UserRepository;
import com.example.facebookbackend.services.FriendServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FriendServicesImpl implements FriendServices {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FriendRequestRepository friendRequestRepository;

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
            for (FriendRequest req: receivedRequestList) {
                if (req.getSender().equals(receiver.get())){
                    return ResponseEntity.badRequest().body(new MessageResponse("Người này đã gửi lời mời kết bạn đến bạn. Hãy đồng ý"));
                }
            }
            List<FriendRequest> sentRequestList = friendRequestRepository.findFriendRequestsBySender(sender.get());
            for (FriendRequest req: sentRequestList) {
                if (req.getReceiver().equals(receiver.get())){
                    return ResponseEntity.badRequest().body(new MessageResponse("Bạn đã gửi lời mời kết bạn đến người này. Hãy chờ họ đồng ý"));
                }
            }
            if (addFriendRequest.getMessage()==null||addFriendRequest.getMessage().equals("")){
                addFriendRequest.setMessage("Xin chào, tôi là "+sender.get().getFullName()+". Chúng ta kết bạn nhé!");
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
    public ResponseEntity<?> getSentAddFriendRequestList(String email) {
        Optional<User> sentUser = userRepository.findByEmail(email);
        if (sentUser.isEmpty() || !sentUser.get().isEnable()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Không tồn tại người dùng có email: " + email));
        } else{
            List<FriendRequest> friendRequestList = friendRequestRepository.findFriendRequestsBySender(sentUser.get());
            return ResponseEntity.ok().body(friendRequestList);
        }
    }

    @Override
    public ResponseEntity<?> getReceivedAddFriendRequestList(String email) {
        Optional<User> receivedUser = userRepository.findByEmail(email);
        if (receivedUser.isEmpty() || !receivedUser.get().isEnable()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Không tồn tại người dùng có email: " + email));
        } else{
            List<FriendRequest> friendRequestList = friendRequestRepository.findFriendRequestsByReceiver(receivedUser.get());
            return ResponseEntity.ok().body(friendRequestList);
        }
    }

    @Override
    public ResponseEntity<?> getUserFriendList(String email) {
        return null;
    }

}
