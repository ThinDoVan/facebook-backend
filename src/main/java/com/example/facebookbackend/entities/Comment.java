package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
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

    private int countLike=0;

    public Comment(Post post, User createdUser, LocalDateTime createdTime, Comment parentComment) {
        this.post = post;
        this.createdUser = createdUser;
        this.createdTime = createdTime;
        this.parentComment = parentComment;
    }
}
