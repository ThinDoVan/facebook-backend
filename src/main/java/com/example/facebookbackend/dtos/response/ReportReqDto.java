package com.example.facebookbackend.dtos.response;

import com.example.facebookbackend.enums.ERequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportReqDto {
    private int reportId;
    private Object itemReported;
//    private UserDto user;
    private UserDto createdUser;
    private LocalDateTime createdTime;
    private String reason;

    private UserDto admin;
    private LocalDateTime processedTime;
    private ERequestStatus requestStatus;
    private String action;
}
