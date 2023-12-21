package com.example.facebookbackend.dtos.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
public class UserDto {
    private int userId;
    private String fullname;
    private LocalDate dateOfBirth;
    private String gender;

}
