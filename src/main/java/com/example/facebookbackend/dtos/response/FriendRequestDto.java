package com.example.facebookbackend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private int id;
    private UserDto sender;
    private UserDto receiver;
    private String message;
}
