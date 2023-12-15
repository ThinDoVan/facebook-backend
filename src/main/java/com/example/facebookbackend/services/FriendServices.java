package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import org.springframework.http.ResponseEntity;

public interface FriendServices {
    ResponseEntity<MessageResponse> createAddFriendRequest(String fromEmail, AddFriendRequest addFriendRequest);
    ResponseEntity<?> getSentAddFriendRequestList(String email);
    ResponseEntity<?> getReceivedAddFriendRequestList(String email);
    ResponseEntity<?> getUserFriendList(String email);
}
