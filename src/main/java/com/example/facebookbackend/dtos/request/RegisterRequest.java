package com.example.facebookbackend.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotEmpty(message = "Tên không được để trống")
    private String fullName;
    @NotEmpty(message = "Email không được để trống")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email sai định dạng. VD: example@domain.com")
    private String email;
    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+-=,.<>?/:;]).{8,}$",
            message = "Mật khẩu gồm 8 ký tự, bao gồm chữ thường, chữ hoa, chữ số và ký tự đặc biệt : `~!@#$%^&*-_=+,./\\|<>?")
    private String password;
    private LocalDate dateOfBirth;
    private String gender;

}
