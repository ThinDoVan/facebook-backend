package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.request.PostRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.services.PostServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/post")
public class PostController {
    @Autowired
    PostServices postServices;

    @PostMapping(path = "/CreatePost")
    public ResponseEntity<MessageResponse> createPost(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody PostRequest postRequest) {
        String email = userDetails.getUsername();
        return postServices.createPost(email, postRequest);
    }

    @GetMapping(path = "/GetPost")
    public ResponseEntity<?> getPost(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestParam int postId) {
        String email = userDetails.getUsername();
        return postServices.getPost(email, postId);
    }

    @PutMapping(path = "/UpdatePost")
    public ResponseEntity<MessageResponse> updatePost(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestParam Integer postId,
                                                      @RequestBody PostRequest postRequest) {
        String email = userDetails.getUsername();
        return postServices.updatePost(email, postId, postRequest);
    }

    @DeleteMapping(path = "/DeletePost")
    public ResponseEntity<MessageResponse> deletePost(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestParam Integer postId) {
        String email = userDetails.getUsername();
        return postServices.deletePost(email, postId);
    }
}
