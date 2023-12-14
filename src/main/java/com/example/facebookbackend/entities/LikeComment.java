package com.example.facebookbackend.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "likeComment")
public class LikeComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int likeCommentId;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    @ManyToOne
    @JoinColumn(name = "commentId")
    private Comment comment;
    private LocalDateTime createdTime;
}
