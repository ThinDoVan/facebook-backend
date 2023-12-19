package com.example.facebookbackend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private int userId;
    private String fullname;
    private LocalDate dateOfBirth;
    private String gender;

}
