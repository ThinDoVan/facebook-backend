package com.example.facebookbackend.services;

import com.example.facebookbackend.dtos.response.ImageDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ImageServices {
    MessageResponse uploadImage(User currentUser, MultipartFile multipartFile, String imageType);
//    ResponseEntity<MessageResponse> uploadImage(User currentUser, MultipartFile multipartFile);
    //    ResponseEntity<?> getImage(int imageId) throws IOException;
    ImageDto getImageInfo(int imageId);
    Page<ImageDto> getUserImageList(int userId, Integer page, Integer size);
    ImageDto getUserProfilePicture(int userId);
    ImageDto getUserCoverPhoto(int userId);
}
