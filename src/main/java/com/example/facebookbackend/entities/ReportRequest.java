package com.example.facebookbackend.entities;

import com.example.facebookbackend.enums.ERequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reportRequest")
@Data
@NoArgsConstructor
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
    private User user;
    @ManyToOne
    @JoinColumn(name = "createdUserId")
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
    private ERequestStatus requestStatus=ERequestStatus.PENDING;
    private String action;

    public ReportRequest(Post post, User createdUser, LocalDateTime createdTime, String reason) {
        this.post = post;
        this.createdUser = createdUser;
        this.createdTime = createdTime;
        this.reason = reason;
    }

    public ReportRequest(User user, User createdUser, LocalDateTime createdTime, String reason) {
        this.user = user;
        this.createdUser = createdUser;
        this.createdTime = createdTime;
        this.reason = reason;
    }
}
