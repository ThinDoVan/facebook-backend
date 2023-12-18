package com.example.facebookbackend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipDto {
    private int id;
    private UserDto user1;
    private UserDto user2;
    private LocalDateTime since;
}
