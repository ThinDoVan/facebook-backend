package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.request.ReportRequestDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.ReportReqDto;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.ReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/report")
//@Tag(name = "admin")
public class ReportControllers {
    @Autowired
    ReportServices reportServices;

    @GetMapping(path = "/GetListPostReport")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getListPostReport(
            @RequestParam(required = false) Integer postId,
            @RequestParam(required = false) String reportStatus,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        Page<ReportReqDto> result = reportServices.getListReportPostRequest(postId, reportStatus, page, size);
        return ResponseEntity.ok(result);
    }
    @GetMapping(path = "/GetListUserReport")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getListUserReport(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String reportStatus,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        Page<ReportReqDto> result = reportServices.getListReportUserRequest(userId, reportStatus, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/GetReportUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getReportUser(@RequestParam Integer reportId) {
        ReportReqDto result = reportServices.getReportUserRequest(reportId);
        return ResponseEntity.ok(result);
    }
    @GetMapping(path = "/GetReportPost")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getReportPost(@RequestParam Integer reportId) {
        ReportReqDto result = reportServices.getReportPostRequest(reportId);
        return ResponseEntity.ok(result);
    }

    @PutMapping(path = "/ReportHandling")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> reportHandling(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam Integer reportId,
                                            @RequestParam Boolean isApproved) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(reportServices.handleReportUser(currentUser, reportId, isApproved));
    }
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(path = "/ReportPost")
    public ResponseEntity<MessageResponse> reportPost(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody ReportRequestDto reportRequestDto) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(reportServices.reportPost(currentUser, reportRequestDto));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(path = "/ReportUser")
    public ResponseEntity<MessageResponse> reportUser(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody ReportRequestDto reportRequestDto) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(reportServices.reportUser(currentUser, reportRequestDto));
    }

}
