package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "postVersion")
public class PostVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postVersionId;
    @ManyToOne
    @JoinColumn(name = "postId")
    @JsonIgnoreProperties
    private Post post;
    private String content;
    private LocalDateTime modifiedTime;
}
