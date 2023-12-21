package com.example.facebookbackend.dtos.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class CommentDto {
    private int commentId;
    private UserDto createdUser;
    private String content;
    private LocalDateTime createdTime;
    private int countLikes;
    private int countComments;
    private CommentDto parrentComment;
    private List<CommentDto> childComment;
}
