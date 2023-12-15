package com.example.facebookbackend.dtos.request;

import com.example.facebookbackend.entities.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddFriendRequest {
    @NotEmpty
    private String toUserEmail;
    private String message;

}
