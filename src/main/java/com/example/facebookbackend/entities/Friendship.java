package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendship")
@Data
@NoArgsConstructor
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

    public Friendship(User user1, User user2, LocalDateTime since) {
        this.user1 = user1;
        this.user2 = user2;
        this.since = since;
    }
}
