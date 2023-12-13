package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendRequest")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int friendRequestId;
    @ManyToOne
    @JoinColumn(name = "fromUserId")
    @JsonIgnoreProperties
    private User fromUser;
    @OneToOne
    private User toUser;
    private String message;
    private LocalDateTime createdTime;
}
