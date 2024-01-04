package com.example.facebookbackend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Data
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

    //Thời gian vô hiệu hóa
    private LocalDateTime lockUntil;

    private LocalDateTime createdTime;
    private String address;
    private String city;
    private String school;
    private String company;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "roleId")})
    private Set<Role> roleSet;
}
