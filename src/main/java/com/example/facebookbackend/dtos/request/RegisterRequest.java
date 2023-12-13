package com.example.facebookbackend.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class RegisterRequest {
    @NotBlank
    private String fullName;
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email sai định dạng. VD: example@domain.com")
    private String email;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*\\\\-_+=,.\\/\\\\|<>?]).{8,}$\n",
            message = "Mật khẩu gồm 8 ký tự, bao gồm chữ thường, chữ hoa, chữ số và ký tự đặc biệt : `~!@#$%^&*-_=+,./\\|<>?")
    private String password;
    private LocalDate dateOfBirth;
    private String gender;
}
