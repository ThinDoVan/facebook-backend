package com.example.facebookbackend.dtos.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordRequest {
    private String currentPassword;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+-=,.<>?/:;]).{8,}$",
            message = "Mật khẩu gồm 8 ký tự, bao gồm chữ thường, chữ hoa, chữ số và ký tự đặc biệt : `~!@#$%^&*-_=+,./\\|<>?")
    private String newPassword;
}
