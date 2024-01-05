package com.example.facebookbackend.dtos.response;

import com.example.facebookbackend.enums.EAudience;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PostDto {
    private int postId;
    private String content;
    private List<ImageDto> imageDtoList;
    private LocalDateTime createdDate;
    private UserDto createdUser;
    private EAudience audience;
    private int countLike;
    private int countComment;
}
