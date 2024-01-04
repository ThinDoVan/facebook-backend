package com.example.facebookbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class VerificationCode {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int verificationCodeId;

    private String verificationCode;
    private LocalDateTime expiredTime;

    @OneToOne(fetch = FetchType.EAGER,targetEntity = User.class)
    @JoinColumn(nullable = false, name = "userId")
    private User user;

    public VerificationCode(String verificationCode, User user) {
        this.verificationCode = verificationCode;
        this.user = user;
    }
}