package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.PostRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import org.springframework.http.ResponseEntity;

public interface PostServices {
    ResponseEntity<MessageResponse> createPost(String email, PostRequest postRequest);

    ResponseEntity<?> getPost(String email, int postId);

    ResponseEntity<MessageResponse> updatePost(String email, Integer postId, PostRequest postRequest);

    ResponseEntity<MessageResponse> deletePost(String email, Integer postId);

}
