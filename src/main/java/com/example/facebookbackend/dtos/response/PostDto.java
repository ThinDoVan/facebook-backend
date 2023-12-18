package com.example.facebookbackend.dtos.response;

import com.example.facebookbackend.enums.EAudience;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PostDto {
    private int postId;
    private String content;
    private LocalDateTime createdDate;
    private UserDto createdUser;
    private EAudience audience;

    public PostDto(int postId, String content, LocalDateTime createdDate, UserDto createdUser, EAudience audience) {
        this.postId = postId;
        this.content = content;
        this.createdDate = createdDate;
        this.createdUser = createdUser;
        this.audience = audience;
    }
}
