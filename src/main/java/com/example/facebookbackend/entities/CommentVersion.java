package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "commentVersion")
@Data
@NoArgsConstructor
public class CommentVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentVersionId;

    @ManyToOne
    @JoinColumn(name = "commentId")
    @JsonBackReference
    private Comment comment;

    private String content;

    private LocalDateTime modifiedTime;

    public CommentVersion(Comment comment, String content, LocalDateTime modifiedTime) {
        this.comment = comment;
        this.content = content;
        this.modifiedTime = modifiedTime;
    }
}
