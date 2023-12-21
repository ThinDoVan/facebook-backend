package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.LoginRequest;
import com.example.facebookbackend.dtos.request.RegisterRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import org.springframework.http.ResponseEntity;

public interface UserServices {
    ResponseEntity<MessageResponse> registerAccount(RegisterRequest registerRequest);
    ResponseEntity<?> loginAccount(LoginRequest loginRequest);
    ResponseEntity<?> getUserByEmail(String email);
    ResponseEntity<?> getUserById(Integer userId);
}
