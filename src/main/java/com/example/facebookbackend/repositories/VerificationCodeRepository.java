package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.VerificationCode;
import com.example.facebookbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {
    Optional<VerificationCode> findByVerificationCode(String verificationCode);
    Optional<VerificationCode> findByUser(User user);
}
