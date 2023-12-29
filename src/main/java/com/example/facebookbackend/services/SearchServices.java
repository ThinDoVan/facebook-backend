package com.example.facebookbackend.services;

import com.example.facebookbackend.entities.User;
import org.springframework.http.ResponseEntity;

public interface SearchServices {
    ResponseEntity<?> searchUser(User currentUser, String name, String relationship, String city, String school, String company, Integer page, Integer size);
    ResponseEntity<?> searchPost(User currentUser, String keyword, String postedBy, Integer postYear, Integer page, Integer size);

}
