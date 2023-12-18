package com.example.facebookbackend.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PostRequest {
    private String content;
    private String audience;
}
