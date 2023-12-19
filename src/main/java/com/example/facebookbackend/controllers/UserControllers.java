package com.example.facebookbackend.controllers;

import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/user")
public class UserControllers {
    @Autowired
    UserServices userServices;


    @GetMapping(path = "/FindUser")
    public ResponseEntity<?> getUser(@RequestParam String userEmail){
        return userServices.getUserByEmail(userEmail);
    }

    @GetMapping(path = "/CurrentUser")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok().body(((UserDetailsImpl) userDetails).getUser());
    }

}
