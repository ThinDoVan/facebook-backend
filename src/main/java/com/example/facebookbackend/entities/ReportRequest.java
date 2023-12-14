package com.example.facebookbackend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "reportRequest")
public class ReportRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportRequestId;
    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    private String reason;
    @ManyToOne
    @JoinColumn(name = "reportStatusId")
    private ReportStatus reportStatus;
    @ManyToOne
    @JoinColumn(name = "adminId")
    private User admin;
}
