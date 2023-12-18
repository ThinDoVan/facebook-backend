package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

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
    private User user;

    private String reason;

    @ManyToOne
    @JoinColumn(name = "reportStatusId")
    @JsonIgnoreProperties
    private ReportStatus reportStatus;

    @ManyToOne
    @JoinColumn(name = "adminId")
    @JsonIgnoreProperties
    private User admin;

}
