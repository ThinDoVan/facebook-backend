package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "commentVersion")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
