package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.request.CommentRequest;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.ReactServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/react")
public class ReactControllers {
    @Autowired
    private ReactServices reactServices;

    @PostMapping(path = "/LikePost")
    public ResponseEntity<?> likePost(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam Integer postId) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return reactServices.likePost(currentUser, postId);
    }

    @PostMapping(path = "/CommentPost")
    public ResponseEntity<?> commentPost(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody CommentRequest commentRequest) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return reactServices.commentPost(currentUser, commentRequest);
    }

    @PostMapping(path = "/LikeComment")
    public ResponseEntity<?> likeComment(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestParam Integer commentId) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return reactServices.likeComment(currentUser, commentId);
    }

    @PostMapping(path = "/ReplyComment")
    public ResponseEntity<?> replyComment(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestBody CommentRequest commentRequest) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return reactServices.replyComment(currentUser, commentRequest);
    }

    @PutMapping(path = "/UpdateComment")
    public ResponseEntity<?> updateComment(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody CommentRequest commentRequest){
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return reactServices.updateComment(currentUser, commentRequest);
    }

    @DeleteMapping(path = "/DeleteComment")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestParam Integer commentId){
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return reactServices.deleteComment(currentUser, commentId);

    }
}
