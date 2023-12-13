package com.example.facebookbackend.dtos.request;

import jakarta.validation.constraints.NotNull;

public class LoginRequest {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
