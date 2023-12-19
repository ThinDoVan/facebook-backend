package com.example.facebookbackend.dtos.request;

import lombok.Data;

@Data
public class CommentRequest {
    private int repliedItemId;
    private String content;
}
