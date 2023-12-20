package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    private LocalDateTime createdTime;

    private boolean deleted = false;

    private LocalDateTime deletedTime;

    @ManyToOne
    @JoinColumn(name = "deletedByUserId")
    @JsonBackReference
    private User deletedUser;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User createdUser;

    @ManyToOne()
    @JoinColumn(name = "audienceId")
    @JsonBackReference
    private Audience audience;


}
