package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.services.FriendServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/friend")
public class FriendControllers {
    @Autowired
    FriendServices friendServices;

    @PostMapping(path = "request/AddNew")
    public ResponseEntity<MessageResponse> sendAddFriendRequest(@AuthenticationPrincipal UserDetails userDetails,
                                                                @RequestBody AddFriendRequest addFriendRequest) {
        String fromEmail = userDetails.getUsername();
        return friendServices.createAddFriendRequest(fromEmail, addFriendRequest);
    }
    @GetMapping(path = "getFriendList")
    public ResponseEntity<?> getUserFriendList(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return friendServices.getUserFriendList(email);
    }
    @GetMapping(path = "request/SentList")
    public ResponseEntity<?> getSentAddFriendRequestList(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return friendServices.getSentAddFriendRequestList(email);
    }
        @GetMapping(path = "request/ReceivedList")
    public ResponseEntity<?> getReceivedAddFriendRequestList(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return friendServices.getReceivedAddFriendRequestList(email);
    }
}
