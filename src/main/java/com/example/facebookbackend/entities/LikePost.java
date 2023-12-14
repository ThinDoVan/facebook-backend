package com.example.facebookbackend.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "likePost")
public class LikePost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int likePostId;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
    private LocalDateTime createdTime;
}
