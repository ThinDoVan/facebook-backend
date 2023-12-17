package com.example.facebookbackend.dtos.response;

import lombok.Data;

import java.util.Set;
@Data
public class JwtResponse {
    private String token;
    private String type="Bearer";
    private String email;
    private Set<String> roles;
    private String mess;

    public JwtResponse() {
    }

    public JwtResponse(String token, String email, Set<String> roles, String mess) {
        this.token = token;
        this.email = email;
        this.roles = roles;
        this.mess = mess;
    }

}
