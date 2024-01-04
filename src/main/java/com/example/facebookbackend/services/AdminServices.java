package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.ReportReqDto;
import com.example.facebookbackend.entities.User;
import org.springframework.data.domain.Page;

public interface AdminServices {
    Page<ReportReqDto> getListPostReport(Integer postId, Integer page, Integer size);
    ReportReqDto getReportRequest(Integer reportId);
    MessageResponse handleReport(User admin, Integer reportId, Boolean isApproved);
}
