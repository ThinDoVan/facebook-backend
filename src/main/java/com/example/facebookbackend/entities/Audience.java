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

    public int getAudienceId() {
        return audienceId;
    }

    public void setAudienceId(int audienceId) {
        this.audienceId = audienceId;
    }

    public String getAudienceType() {
        return audienceType;
    }

    public void setAudienceType(String audienceType) {
        this.audienceType = audienceType;
    }

    public Set<Post> getPostSet() {
        return postSet;
    }

    public void setPostSet(Set<Post> postSet) {
        this.postSet = postSet;
    }
}
