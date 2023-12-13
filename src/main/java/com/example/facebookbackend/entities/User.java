package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String fullName;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private String gender;
    private boolean enable;
    @OneToOne
    @JoinColumn(name = "roleId")
    @JsonIgnoreProperties
    private Role role;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdUser")
    private Set<Post> postSet;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fromUser")
    private Set<FriendRequest> friendRequestSet;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user1")
    private Set<Friendship> friendshipSet;
}
