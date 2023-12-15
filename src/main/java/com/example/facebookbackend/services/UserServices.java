package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.request.LoginRequest;
import com.example.facebookbackend.dtos.request.RegisterRequest;
import com.example.facebookbackend.dtos.response.JwtResponse;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.FriendRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserServices {
    ResponseEntity<MessageResponse> registerAccount(RegisterRequest registerRequest);
    ResponseEntity<?> loginAccount(LoginRequest loginRequest);
    ResponseEntity<?> getUser(String email);

}
