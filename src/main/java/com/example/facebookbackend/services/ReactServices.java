package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.CommentRequest;
import com.example.facebookbackend.dtos.request.ReportRequestDto;
import com.example.facebookbackend.dtos.response.CommentDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import org.springframework.data.domain.Page;

public interface ReactServices {
    MessageResponse likePost(User currentUser, Integer postId);
    MessageResponse commentPost(User currentUser, CommentRequest commentRequest);
    MessageResponse likeComment(User currentUser, Integer commentId);
    MessageResponse replyComment(User currentUser, CommentRequest commentRequest);
    MessageResponse updateComment(User currentUser, CommentRequest commentRequest);
    MessageResponse deleteComment(User currentUser, Integer commentId);
    Page<CommentDto> getPostComments(User currentUser, Integer postId, Integer page, Integer size);

}
