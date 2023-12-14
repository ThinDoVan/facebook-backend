package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "user",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
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
    @OneToMany
    @JoinColumn(name = "roleId")
    @JsonIgnore
    private Set<Role> roleSet;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "createdUser")
    private Set<Post> postSet;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fromUser")
    private Set<FriendRequest> friendRequestSet;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user1")
    private Set<Friendship> friendshipSet;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Set<Role> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<Role> roleSet) {
        this.roleSet = roleSet;
    }

    public Set<Post> getPostSet() {
        return postSet;
    }

    public void setPostSet(Set<Post> postSet) {
        this.postSet = postSet;
    }

    public Set<FriendRequest> getFriendRequestSet() {
        return friendRequestSet;
    }

    public void setFriendRequestSet(Set<FriendRequest> friendRequestSet) {
        this.friendRequestSet = friendRequestSet;
    }

    public Set<Friendship> getFriendshipSet() {
        return friendshipSet;
    }

    public void setFriendshipSet(Set<Friendship> friendshipSet) {
        this.friendshipSet = friendshipSet;
    }
}
