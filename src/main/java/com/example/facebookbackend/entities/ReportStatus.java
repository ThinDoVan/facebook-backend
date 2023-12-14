package com.example.facebookbackend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "reportStatus")
public class ReportStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportStatusId;
    private String statusName;

    public int getReportStatusId() {
        return reportStatusId;
    }

    public void setReportStatusId(int reportStatusId) {
        this.reportStatusId = reportStatusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
