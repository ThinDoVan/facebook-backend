package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.request.AddFriendRequest;
import com.example.facebookbackend.dtos.response.FriendRequestDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.UserDto;
import com.example.facebookbackend.entities.User;
import org.springframework.data.domain.Page;

public interface FriendServices {
    MessageResponse createAddFriendRequest(User sender, AddFriendRequest addFriendRequest);
    Page<FriendRequestDto> getSentAddFriendRequestList(User currentUser, Integer page, Integer size);
    Page<FriendRequestDto> getReceivedAddFriendRequestList(User currentUser, Integer page, Integer size);
    FriendRequestDto getFriendRequest(User currentUser, int id);
    MessageResponse respondFriendRequest(User currentUser, int id, boolean isAccept);
    Page<UserDto> getUserFriendList(User currentUser, Integer page, Integer size);
}
