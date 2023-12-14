package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Audience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudienceRepository extends JpaRepository<Audience, Integer> {
}