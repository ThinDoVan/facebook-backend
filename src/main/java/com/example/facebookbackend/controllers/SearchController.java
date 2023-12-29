package com.example.facebookbackend.controllers;

import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.SearchServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/search")
public class SearchController {
    @Autowired
    SearchServices searchServices;

    @GetMapping(path = "/user")
    public ResponseEntity<?> searchUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam String name,
                                        @RequestParam(required = false, defaultValue = "") String relationship,
                                        @RequestParam(required = false, defaultValue = "") String city,
                                        @RequestParam(required = false, defaultValue = "") String school,
                                        @RequestParam(required = false, defaultValue = "") String company,
                                        @RequestParam(required = false, defaultValue = "0") Integer page,
                                        @RequestParam(required = false, defaultValue = "5") Integer size) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return searchServices.searchUser(currentUser, name, relationship, city, school, company, page, size);
    }

    @GetMapping(path = "/post")
    public ResponseEntity<?> searchPost(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam String keyword,
                                        @RequestParam(required = false, defaultValue = "") String postedBy,
                                        @RequestParam(required = false, defaultValue = "") Integer postYear,
                                        @RequestParam(required = false, defaultValue = "0") Integer page,
                                        @RequestParam(required = false, defaultValue = "5") Integer size){
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return searchServices.searchPost(currentUser, keyword, postedBy, postYear, page, size);
    }
    
}
