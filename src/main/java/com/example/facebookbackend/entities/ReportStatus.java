package com.example.facebookbackend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reportStatus")
@Data
public class ReportStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportStatusId;

    private String statusName;

}
