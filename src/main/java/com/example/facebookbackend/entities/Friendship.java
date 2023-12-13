package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendship")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int friendshipId;
    @ManyToOne
    @JoinColumn(name = "user1")
    @JsonIgnoreProperties
    private User user1;
    @ManyToOne
    @JoinColumn(name = "user2")
    @JsonIgnoreProperties
    private User user2;
    private LocalDateTime since;
}
