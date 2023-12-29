package com.example.facebookbackend.dtos.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
public class UserDto {
    private String fullname;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String school;
    private String company;
}
