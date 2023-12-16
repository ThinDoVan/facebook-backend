package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.response.ListResponse;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.Friendship;
import org.springframework.http.ResponseEntity;

public interface FriendServices {
    ResponseEntity<MessageResponse> createAddFriendRequest(String fromEmail, AddFriendRequest addFriendRequest);
    ListResponse getSentAddFriendRequestList(String email, Integer page, Integer size);
    ListResponse getReceivedAddFriendRequestList(String email, Integer page, Integer size);
    ResponseEntity<?> respondFriendRequest(String email, int id, Boolean isAccept);
    ListResponse getUserFriendList(String email, Integer page, Integer size);
}
