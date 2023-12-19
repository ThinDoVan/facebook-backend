package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import org.springframework.http.ResponseEntity;

public interface FriendServices {
    ResponseEntity<MessageResponse> createAddFriendRequest(User sender, AddFriendRequest addFriendRequest);
    ResponseEntity<?> getSentAddFriendRequestList(User currentUser, Integer page, Integer size);
    ResponseEntity<?> getReceivedAddFriendRequestList(User currentUser, Integer page, Integer size);
    ResponseEntity<?> respondFriendRequest(User currentUser, int id, Boolean isAccept);
    ResponseEntity<?> getUserFriendList(User currentUser, Integer page, Integer size);
}
