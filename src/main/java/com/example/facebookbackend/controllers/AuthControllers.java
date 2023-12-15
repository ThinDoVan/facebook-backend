package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.request.LoginRequest;
import com.example.facebookbackend.dtos.request.RegisterRequest;
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
        return userServices.registerAccount(registerRequest);
    }

    @PostMapping(path = "/Login")
    public ResponseEntity<?> loginAccount(@Valid @RequestBody LoginRequest loginRequest){
        return userServices.loginAccount(loginRequest);
    }

}
