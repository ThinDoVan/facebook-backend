package com.example.facebookbackend.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddFriendRequest {
    @NotEmpty
    private int receivedUserId;
    private String message;
}
