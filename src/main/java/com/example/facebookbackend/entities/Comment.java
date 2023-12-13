package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;
    @ManyToOne
    @JoinColumn(name = "postId")
    @JsonIgnoreProperties
    private Post post;
    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties
    private User user;
    private String content;
    private LocalDateTime createdTime;
    private Integer parentCommentId=null;
}
