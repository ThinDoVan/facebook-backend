package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "reportRequest")
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

    public int getReportRequestId() {
        return reportRequestId;
    }

    public void setReportRequestId(int reportRequestId) {
        this.reportRequestId = reportRequestId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }
}
