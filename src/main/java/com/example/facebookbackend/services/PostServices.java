package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.PostRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.PostDto;
import com.example.facebookbackend.entities.User;
import org.springframework.data.domain.Page;

public interface PostServices {
    MessageResponse createPost(User currentUser, PostRequest postRequest);
    PostDto getPost(User currentUser, int postId);
    Page<PostDto> getUserPostList(User currentUser, Integer userId, Integer page, Integer size);
    MessageResponse updatePost(User currentUser, Integer postId, PostRequest postRequest);
    MessageResponse deletePost(User currentUser, Integer postId);
}
