package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.PostRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import org.springframework.http.ResponseEntity;

public interface PostServices {
    ResponseEntity<MessageResponse> createPost(String currentUserEmail, PostRequest postRequest);

    ResponseEntity<?> getPost(String currentUserEmail, int postId);
    ResponseEntity<?> getUserPostList(String currentUserEmail, Integer userId, Integer page, Integer size);

    ResponseEntity<MessageResponse> updatePost(String currentUserEmail, Integer postId, PostRequest postRequest);

    ResponseEntity<MessageResponse> deletePost(String currentUserEmail, Integer postId);

}
