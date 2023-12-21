package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.ImageServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/image")
public class ImageControllers {
    @Autowired
    ImageServices imageServices;

    @PostMapping(path = "/UploadPhoto")
    public ResponseEntity<?> updatePicture(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestParam MultipartFile file,
                                           @RequestParam String imageType) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        try {
            return imageServices.uploadImage(currentUser, file, imageType);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Upload ảnh thất bại"));
        }
    }


//    @GetMapping(path = "/GetImage")
//    public ResponseEntity<?> getImage(@RequestParam int imageId) {
//        try {
//            return imageServices.getImage(imageId);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Tải ảnh thất bại"));
//        }
//    }

    @GetMapping(path = "/GetImageInfo")
    public ResponseEntity<?> getImageInfo(@RequestParam int imageId) {
        return imageServices.getImageInfo(imageId);
    }

    @GetMapping(path = "/GetUserImageList")
    public ResponseEntity<?> getUserImageList(@RequestParam int userId,
                                              @RequestParam(required = false, defaultValue = "0") Integer page,
                                              @RequestParam(required = false, defaultValue = "5") Integer size) {
        return imageServices.getUserImageList(userId, page, size);
    }
}
