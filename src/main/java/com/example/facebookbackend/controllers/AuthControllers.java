package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.request.LoginRequest;
import com.example.facebookbackend.dtos.request.RegisterRequest;
import com.example.facebookbackend.dtos.request.ResetPasswordRequest;
import com.example.facebookbackend.dtos.response.JwtResponse;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/auth")
public class AuthControllers {
    @Autowired
    private UserServices userServices;

    @PostMapping(path = "/Register")
    public ResponseEntity<MessageResponse> registerAccount(@Valid @RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(userServices.registerAccount(registerRequest));
    }
    @PostMapping(path = "/ActiveAccount")
    public ResponseEntity<MessageResponse> activeAccount(@RequestParam String verificationCode){
        return ResponseEntity.ok(userServices.activeAccount(verificationCode));
    }
    @PostMapping(path = "/Login")
    public ResponseEntity<JwtResponse> loginAccount(@Valid @RequestBody LoginRequest loginRequest){
        JwtResponse response = userServices.loginAccount(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/ForgetPassword")
    public ResponseEntity<MessageResponse> getResetPasswordToken(@RequestParam String userEmail){
        return ResponseEntity.ok(userServices.forgotPassword(userEmail));
    }

    @PostMapping(path = "/ResetPassword")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        return ResponseEntity.ok(userServices.resetPassword(resetPasswordRequest));
    }


}
