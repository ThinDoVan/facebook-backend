package com.example.facebookbackend.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

public class LoginRequest {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
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
}
