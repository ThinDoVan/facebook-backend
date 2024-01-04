package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.response.ReportReqDto;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.AdminServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/admin")
@Tag(name = "admin")
public class AdminControllers {
    @Autowired
    AdminServices adminServices;

    @GetMapping(path = "/GetListReport")
    public ResponseEntity<?> getListReport(
            @RequestParam(required = false) Integer postId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        Page<ReportReqDto> result = adminServices.getListPostReport(postId, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/GetReport")
    public ResponseEntity<?> getListReport(@RequestParam Integer reportId) {
        ReportReqDto result = adminServices.getReportRequest(reportId);
        return ResponseEntity.ok(result);
    }

    @PutMapping(path = "/ReportHandling")
    public ResponseEntity<?> reportHandling(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam Integer reportId,
                                            @RequestParam Boolean isAprroved) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(adminServices.handleReport(currentUser, reportId, isAprroved));
    }
}
