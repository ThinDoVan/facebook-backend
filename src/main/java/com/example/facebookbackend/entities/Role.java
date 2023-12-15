package com.example.facebookbackend.entities;

import com.example.facebookbackend.enums.ERole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "role")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleId;

    @Enumerated(EnumType.STRING)
    private ERole role;

}
