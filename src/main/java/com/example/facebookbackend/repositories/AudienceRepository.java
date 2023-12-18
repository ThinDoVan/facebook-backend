package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Audience;
import com.example.facebookbackend.enums.EAudience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AudienceRepository extends JpaRepository<Audience, Integer> {
    Optional<Audience> findByAudienceType(EAudience audienceType);
}
