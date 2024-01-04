package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.ReportRequestDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.ReportReqDto;
import com.example.facebookbackend.entities.User;
import org.springframework.data.domain.Page;

public interface ReportServices {
    MessageResponse reportPost(User currentUser, ReportRequestDto reportRequestDto);

    MessageResponse reportUser(User currentUser, ReportRequestDto reportRequestDto);

    Page<ReportReqDto> getListReport(Integer postId, Integer userId, String requestStatus, Integer page, Integer size);

    ReportReqDto getReportRequest(Integer reportId);

    MessageResponse handleReport(User admin, Integer reportId, Boolean isApproved);
}
