package com.example.facebookbackend.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "audience")
public class Audience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int audienceId;
    private String audienceType;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "audience")
    private Set<Post> postSet;
}
