package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportStatusRepository extends JpaRepository<ReportStatus, Integer> {
}
