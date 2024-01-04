package com.example.facebookbackend.entities;

import com.example.facebookbackend.enums.ERequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reportRequest")
@Data
public class ReportRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportRequestId;

    @ManyToOne
    @JoinColumn(name = "postId")
    @JsonIgnoreProperties
    private Post post;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties
    private User createdUser;
    private LocalDateTime createdTime;
    private String reason;

    @ManyToOne
    @JoinColumn(name = "adminId")
    @JsonIgnoreProperties
    private User admin;
    private LocalDateTime processedTime;
    @Enumerated(EnumType.STRING)
    private ERequestStatus requestStatus;
    private String action;
}
