package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;
    private LocalDateTime createdTime;
    private boolean deleted;
    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties
    private User createdUser;
    @ManyToOne()
    @JoinColumn(name = "audienceId")
    @JsonIgnoreProperties
    private Audience audience;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private Set<PostVersion> postVersionSet;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private Set<Comment> commentSet;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public Audience getAudience() {
        return audience;
    }

    public void setAudience(Audience audience) {
        this.audience = audience;
    }

    public Set<PostVersion> getPostVersionSet() {
        return postVersionSet;
    }

    public void setPostVersionSet(Set<PostVersion> postVersionSet) {
        this.postVersionSet = postVersionSet;
    }

    public Set<Comment> getCommentSet() {
        return commentSet;
    }

    public void setCommentSet(Set<Comment> commentSet) {
        this.commentSet = commentSet;
    }
}
