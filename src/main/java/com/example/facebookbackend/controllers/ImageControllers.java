package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.response.ImageDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.ImageServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/image")
public class ImageControllers {
    @Autowired
    ImageServices imageServices;

    @PostMapping(path = "/UploadPhoto")
    public ResponseEntity<MessageResponse> updatePicture(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestParam MultipartFile file,
                                                         @RequestParam String imageType) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(imageServices.uploadImage(currentUser, file, imageType, null));
    }

    @GetMapping(path = "/GetImageInfo")
    public ResponseEntity<ImageDto> getImageInfo(@RequestParam int imageId) {
        ImageDto result = imageServices.getImageInfo(imageId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/GetCoverPhotoInfo")
    public ResponseEntity<?> getCoverPhotoInfo(@RequestParam int userId) {
        ImageDto result = imageServices.getUserCoverPhoto(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/GetProfilePictureInfo")
    public ResponseEntity<ImageDto> getProfilePictureInfo(@RequestParam int userId) {
        ImageDto result = imageServices.getUserProfilePicture(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/GetUserImageList")
    public ResponseEntity<Page<ImageDto>> getUserImageList(@RequestParam int userId,
                                                           @RequestParam(required = false, defaultValue = "0") Integer page,
                                                           @RequestParam(required = false, defaultValue = "5") Integer size) {
        Page<ImageDto> result = imageServices.getUserImageList(userId, page, size);
        return ResponseEntity.ok(result);
    }
}
