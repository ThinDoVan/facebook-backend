package com.example.facebookbackend.dtos.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+-=,.<>?/:;]).{8,}$",
            message = "Mật khẩu gồm 8 ký tự, bao gồm chữ thường, chữ hoa, chữ số và ký tự đặc biệt : `~!@#$%^&*-_=+,./\\|<>?")
    private String password;
    private String verificationCode;
}
