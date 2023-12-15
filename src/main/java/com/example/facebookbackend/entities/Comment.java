package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Data
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

    @ManyToOne
    @JoinColumn(name = "parentCommentId")
    @JsonIgnoreProperties
    private Comment parentComment;

}
