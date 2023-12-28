package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.request.PostRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.PostServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/post")
public class PostControllers {
    @Autowired
    PostServices postServices;

    @PostMapping(path = "/CreatePost")
    public ResponseEntity<MessageResponse> createPost(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody PostRequest postRequest) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return postServices.createPost(currentUser, postRequest);
    }

    @GetMapping(path = "/GetPost")
    public ResponseEntity<?> getPost(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestParam int postId) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return postServices.getPost(currentUser, postId);
    }

    @GetMapping(path = "/GetPostList")
    public ResponseEntity<?> getUserPostList(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam Integer userId,
                                             @RequestParam(required = false, defaultValue = "0") Integer page,
                                             @RequestParam(required = false, defaultValue = "5") Integer size) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return postServices.getUserPostList(currentUser, userId, page, size);
    }

    @PutMapping(path = "/UpdatePost")
    public ResponseEntity<MessageResponse> updatePost(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestParam Integer postId,
                                                      @RequestBody PostRequest postRequest) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return postServices.updatePost(currentUser, postId, postRequest);
    }

    @DeleteMapping(path = "/DeletePost")
    public ResponseEntity<MessageResponse> deletePost(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestParam Integer postId) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return postServices.deletePost(currentUser, postId);
    }

}
