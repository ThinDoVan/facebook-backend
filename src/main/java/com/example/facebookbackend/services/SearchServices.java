package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.response.PostDto;
import com.example.facebookbackend.dtos.response.UserDto;
import com.example.facebookbackend.entities.User;
import org.springframework.data.domain.Page;

public interface SearchServices {
    Page<UserDto> searchUser(User currentUser, String name, String relationship, String city, String school, String company, Integer page, Integer size);
    Page<PostDto> searchPost(User currentUser, String keyword, String postedBy, Integer postYear, Integer page, Integer size);
}
