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

    @PostMapping(path = "/request/AddNew")
    public ResponseEntity<MessageResponse> sendAddFriendRequest(@AuthenticationPrincipal UserDetails userDetails,
                                                                @RequestBody AddFriendRequest addFriendRequest) {
        String fromEmail = userDetails.getUsername();
        return friendServices.createAddFriendRequest(fromEmail, addFriendRequest);
    }

    @GetMapping(path = "/request/Sent")
    public ResponseEntity<?> getSentAddFriendRequestList(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestParam(required = false, defaultValue = "0") Integer page,
                                                         @RequestParam(required = false, defaultValue = "5") Integer size) {
        String email = userDetails.getUsername();
        return friendServices.getSentAddFriendRequestList(email, page, size);
    }

    @GetMapping(path = "/request/Received")
    public ResponseEntity<?> getReceivedAddFriendRequestList(@AuthenticationPrincipal UserDetails userDetails,
                                                             @RequestParam(required = false, defaultValue = "0") Integer page,
                                                             @RequestParam(required = false, defaultValue = "5") Integer size) {
        String email = userDetails.getUsername();
        return friendServices.getReceivedAddFriendRequestList(email, page, size);
    }

    @PostMapping(path = "/request")
    public ResponseEntity<?> respondFriendRequest(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestParam int id,
                                                  @RequestParam Boolean isAccept) {
        String email = userDetails.getUsername();
        return friendServices.respondFriendRequest(email, id, isAccept);
    }
    @GetMapping(path = "/request")
    public ResponseEntity<?> getFriendRequest(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestParam int id) {
        String email = userDetails.getUsername();
        return friendServices.respondFriendRequest(email, id, null);
    }

    @GetMapping(path = "/getFriendList")
    public ResponseEntity<?> getUserFriendList(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return friendServices.getUserFriendList(email);
    }

}
