package com.example.facebookbackend.dtos.response;

import lombok.Data;

import java.util.Set;
@Data
public class JwtResponse {
    private String token;
    private String type="Bearer";
    private String email;
    private Set<String> roles;

    public JwtResponse() {
    }

    public JwtResponse(String token, String email, Set<String> roles) {
        this.token = token;
        this.email = email;
        this.roles = roles;
    }

}
