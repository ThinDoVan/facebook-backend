package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "postVersion")
@Data
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

    public PostVersion(Post post, String content, LocalDateTime modifiedTime) {
        this.post = post;
        this.content = content;
        this.modifiedTime = modifiedTime;
    }
}
