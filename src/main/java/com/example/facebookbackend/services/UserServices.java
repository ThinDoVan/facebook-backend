package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.ChangePasswordRequest;
import com.example.facebookbackend.dtos.request.LoginRequest;
import com.example.facebookbackend.dtos.request.RegisterRequest;
import com.example.facebookbackend.dtos.request.ResetPasswordRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import org.springframework.http.ResponseEntity;

public interface UserServices {
    ResponseEntity<MessageResponse> registerAccount(RegisterRequest registerRequest);
    ResponseEntity<?> loginAccount(LoginRequest loginRequest);
    ResponseEntity<MessageResponse> changePassword(User currentUser, ChangePasswordRequest changePasswordRequest);
    ResponseEntity<?> getUserByEmail(String email);
    ResponseEntity<?> getUserById(Integer userId);
    ResponseEntity<MessageResponse> forgotPassword(String email);
    ResponseEntity<MessageResponse> resetPassword(ResetPasswordRequest resetPasswordRequest);

}
