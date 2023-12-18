package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import org.springframework.http.ResponseEntity;

public interface FriendServices {
    ResponseEntity<MessageResponse> createAddFriendRequest(String fromEmail, AddFriendRequest addFriendRequest);
    ResponseEntity<?> getSentAddFriendRequestList(String email, Integer page, Integer size);
    ResponseEntity<?> getReceivedAddFriendRequestList(String email, Integer page, Integer size);
    ResponseEntity<?> respondFriendRequest(String email, int id, Boolean isAccept);
    ResponseEntity<?> getUserFriendList(String email, Integer page, Integer size);
}
