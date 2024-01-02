package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.UserDto;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.UserServices;
import org.modelmapper.ModelMapper;
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
    @Autowired
    ModelMapper modelMapper;

    @GetMapping(path = "/GetByEmail")
    public ResponseEntity<UserDto> getUser(@RequestParam String userEmail) {
        UserDto result = userServices.getUserByEmail(userEmail);
        return ResponseEntity.ok(result);
    }
    @GetMapping(path = "/GetById")
    public ResponseEntity<UserDto> getUser(@RequestParam int userId) {
        UserDto result = userServices.getUserById(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/CurrentUser")
    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(modelMapper.map(((UserDetailsImpl) userDetails).getUser(), UserDto.class));
    }

    @PutMapping(path = "/ChangePassword")
    public ResponseEntity<MessageResponse> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestBody String currentPassword) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(userServices.changePassword(currentUser, currentPassword));
    }

    @PutMapping(path = "/UpdateUserInfo")
    public ResponseEntity<MessageResponse> updateUserInfo(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestBody UserDto userInfo) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(userServices.updateUserInfo(currentUser, userInfo));
    }
}
