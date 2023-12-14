package com.example.facebookbackend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "reportStatus")
public class ReportStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportStatusId;
    private String statusName;

}
