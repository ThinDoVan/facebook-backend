package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private User createdUser;

    private boolean deleted = false;

    private LocalDateTime deletedTime;

    @ManyToOne
    @JoinColumn(name = "deletedByUserId")
    @JsonBackReference
    private User deletedUser;

    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(name = "parentCommentId")
    @JsonIgnoreProperties
    private Comment parentComment;

}
