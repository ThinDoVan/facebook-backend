package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "post")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    private LocalDateTime createdTime;

    private boolean deleted = false;

    private LocalDateTime deletedTime;

    @ManyToOne
    @JoinColumn(name = "deletedByUserId")
    @JsonBackReference
    private User deletedUser;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User createdUser;

    @ManyToOne()
    @JoinColumn(name = "audienceId")
    @JsonBackReference
    private Audience audience;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private Set<Comment> commentSet;
}
