package com.example.facebookbackend.dtos.response;

import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.EAudience;

import java.time.LocalDateTime;

public class PostResponse {
    private int postId;
    private String content;
    private LocalDateTime createdDate;
    private User createdUser;
    private EAudience audience;

    public PostResponse(int postId, String content, LocalDateTime createdDate, User createdUser, EAudience audience) {
        this.postId = postId;
        this.content = content;
        this.createdDate = createdDate;
        this.createdUser = createdUser;
        this.audience = audience;
    }
}
