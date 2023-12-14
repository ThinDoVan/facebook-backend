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

    public int getPostVersionId() {
        return postVersionId;
    }

    public void setPostVersionId(int postVersionId) {
        this.postVersionId = postVersionId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
