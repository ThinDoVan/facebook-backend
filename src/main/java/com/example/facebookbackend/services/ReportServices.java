package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.ReportRequestDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.ReportReqDto;
import com.example.facebookbackend.entities.User;
import org.springframework.data.domain.Page;

public interface ReportServices {
    MessageResponse reportPost(User currentUser, ReportRequestDto reportRequestDto);

    MessageResponse reportUser(User currentUser, ReportRequestDto reportRequestDto);

    Page<ReportReqDto> getListReportUserRequest(Integer userId, String requestStatus, Integer page, Integer size);
    Page<ReportReqDto> getListReportPostRequest(Integer postId, String requestStatus, Integer page, Integer size);


    //    ReportReqDto getReportRequest(Integer reportId);
    ReportReqDto getReportUserRequest(Integer reportId);
    ReportReqDto getReportPostRequest(Integer reportId);

    MessageResponse handleReportUser(User admin, Integer reportId, Boolean isApproved);
    MessageResponse handleReportPost();

}
