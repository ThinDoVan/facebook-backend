package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.PostRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import org.springframework.http.ResponseEntity;

public interface PostServices {
    ResponseEntity<MessageResponse> createPost(User currentUser, PostRequest postRequest);

    ResponseEntity<?> getPost(User currentUser, int postId);
    ResponseEntity<?> getUserPostList(User currentUser, Integer userId, Integer page, Integer size);

    ResponseEntity<MessageResponse> updatePost(User currentUser, Integer postId, PostRequest postRequest);

    ResponseEntity<MessageResponse> deletePost(User currentUser, Integer postId);

}
