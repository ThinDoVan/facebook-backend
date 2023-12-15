package com.example.facebookbackend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@Data
@AllArgsConstructor
public class UserResponse {
    private int userId;
    private String fullname;
    private LocalDate dateOfBirth;
    private String gender;

}
