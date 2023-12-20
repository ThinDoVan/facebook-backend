package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "postVersion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postVersionId;

    @ManyToOne
    @JoinColumn(name = "postId")
    @JsonBackReference
    private Post post;

    private String content;

    private LocalDateTime modifiedTime;

    public PostVersion(Post post, String content, LocalDateTime modifiedTime) {
        this.post = post;
        this.content = content;
        this.modifiedTime = modifiedTime;
    }
}
