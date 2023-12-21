package com.example.facebookbackend.dtos.request;

import lombok.Data;

@Data
public class PostRequest {
    private String content;
    private String audience;
}
