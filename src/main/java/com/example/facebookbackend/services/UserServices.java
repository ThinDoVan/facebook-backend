package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.LoginRequest;
import com.example.facebookbackend.dtos.request.RegisterRequest;
import com.example.facebookbackend.dtos.request.ResetPasswordRequest;
import com.example.facebookbackend.dtos.response.JwtResponse;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.UserDto;
import com.example.facebookbackend.entities.User;

public interface UserServices {
    MessageResponse registerAccount(RegisterRequest registerRequest);
    MessageResponse activeAccount(String verificationCode);
    JwtResponse loginAccount(LoginRequest loginRequest);
    MessageResponse changePassword(User currentUser, String currentPassword);
    UserDto getUserByEmail(String email);
    UserDto getUserById(Integer userId);
    MessageResponse updateUserInfo(User currentUser, UserDto userInfo);
    MessageResponse forgotPassword(String email);
    MessageResponse resetPassword(ResetPasswordRequest resetPasswordRequest);
    MessageResponse disableAccount(User currentUser);
}
