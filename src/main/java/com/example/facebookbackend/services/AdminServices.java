package com.example.facebookbackend.services;

import com.example.facebookbackend.entities.User;
import org.springframework.http.ResponseEntity;

public interface AdminServices {
    ResponseEntity<?> getListPostReport(Integer postId, Integer page, Integer size);
    ResponseEntity<?> getReportRequest(Integer reportId);
    ResponseEntity<?> handleReport(User admin, Integer reportId, Boolean isApproved);
}
