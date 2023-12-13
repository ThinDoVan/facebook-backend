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
}
