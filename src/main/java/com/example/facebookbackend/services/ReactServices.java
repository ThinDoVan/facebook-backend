package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.CommentRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import org.springframework.http.ResponseEntity;

public interface ReactServices {
    ResponseEntity<MessageResponse> likePost(User currentUser, Integer postId);
    ResponseEntity<MessageResponse> commentPost(User currentUser, CommentRequest commentRequest);
    ResponseEntity<MessageResponse> likeComment(User currentUser, Integer commentId);
    ResponseEntity<MessageResponse> replyComment(User currentUser, CommentRequest commentRequest);
    ResponseEntity<MessageResponse> updateComment(User currentUser, CommentRequest commentRequest);
    ResponseEntity<MessageResponse> deleteComment(User currentUser, Integer commentId);
    ResponseEntity<?> getPostComments(User currentUser, Integer postId, Integer page, Integer size);
}
