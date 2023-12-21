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
    private String path;
    private UserDto userDto;
    private LocalDateTime createdTime;
    private EImageType imageType;
}
