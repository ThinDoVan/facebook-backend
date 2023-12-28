package com.example.facebookbackend.dtos.response;

import com.example.facebookbackend.enums.EImageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private UserDto createdUser;
    private LocalDateTime createdTime;
    private String fileName;
    private String contentType;
    private String url;
    private long size;
    private EImageType imageType;
}
