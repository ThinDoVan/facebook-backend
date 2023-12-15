package com.example.facebookbackend.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class RegisterRequest {
    @NotEmpty(message = "Tên không được để trống")
    private String fullName;
    @NotEmpty(message = "Email không được để trống")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email sai định dạng. VD: example@domain.com")
    private String email;
    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+-=,.<>?/\\:;]).{8,}$",
            message = "Mật khẩu gồm 8 ký tự, bao gồm chữ thường, chữ hoa, chữ số và ký tự đặc biệt : `~!@#$%^&*-_=+,./\\|<>?")
    private String password;
    private LocalDate dateOfBirth;
    private String gender;

    public RegisterRequest() {
    }

    public RegisterRequest(String fullName, String email, String password, LocalDate dateOfBirth, String gender) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public RegisterRequest(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
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
}
