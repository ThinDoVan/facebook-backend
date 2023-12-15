package com.example.facebookbackend.entities;

import com.example.facebookbackend.enums.EAudience;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "audience")
@Data
public class Audience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int audienceId;

    @Enumerated(EnumType.STRING)
    private EAudience audienceType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "audience")
    @JsonIgnore
    private Set<Post> postSet;

}
