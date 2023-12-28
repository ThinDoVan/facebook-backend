package com.example.facebookbackend.controllers;

import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.AdminServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/admin")
public class AdminControllers {
    @Autowired
    AdminServices adminServices;

    @GetMapping(path = "/GetListReport")
    public ResponseEntity<?> getListReport(
            @RequestParam(required = false) Integer postId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size) {
        return adminServices.getListPostReport(postId, page, size);
    }

    @GetMapping(path = "/GetReport")
    public ResponseEntity<?> getListReport(@RequestParam Integer reportId) {
        return adminServices.getReportRequest(reportId);
    }

    @PutMapping(path = "/ReportHandling")
    public ResponseEntity<?> reportHandling(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam Integer reportId,
                                            @RequestParam Boolean isAprroved) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return adminServices.handleReport(currentUser, reportId, isAprroved);
    }
}
