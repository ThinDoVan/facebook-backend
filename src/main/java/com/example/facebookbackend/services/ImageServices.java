package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageServices {
    ResponseEntity<MessageResponse> uploadImage(User currentUser, MultipartFile multipartFile, String imageType) throws IOException;

    //    ResponseEntity<?> getImage(int imageId) throws IOException;
    ResponseEntity<?> getImageInfo(int imageId);

    ResponseEntity<?> getUserImageList(int userId, Integer page, Integer size);

    ResponseEntity<?> getUserProfilePicture(int userId);

    ResponseEntity<?> getUserCoverPhoto(int userId);
}
