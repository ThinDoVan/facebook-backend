package com.example.facebookbackend.services.implement;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.facebookbackend.dtos.response.ImageDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.Image;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.EImageType;
import com.example.facebookbackend.exceptions.DataNotFoundException;
import com.example.facebookbackend.exceptions.InvalidDataException;
import com.example.facebookbackend.repositories.ImageRepository;
import com.example.facebookbackend.repositories.UserRepository;
import com.example.facebookbackend.services.ImageServices;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImageServicesImpl implements ImageServices {
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ResponseUtils responseUtils;
    @Autowired
    Cloudinary cloudinary;

    @Override
    public MessageResponse uploadImage(User currentUser, MultipartFile multipartFile, String imageType) {
        if (multipartFile.isEmpty()){
            throw new InvalidDataException("File upload trống");
        }
        System.out.println(multipartFile.getContentType());

        if (!multipartFile.getContentType().startsWith("image/")){
            throw new InvalidDataException("Chỉ được upload file image");
        }
        Map uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Tải ảnh thất bại");
        }
        System.out.println(uploadResult);
        Image image = new Image();
        image.setUser(currentUser);
        image.setCreatedTime(LocalDateTime.now());
        image.setUrl(uploadResult.get("url").toString());
        image.setFileName(multipartFile.getOriginalFilename());
        image.setContentType(multipartFile.getContentType());
        image.setSize(multipartFile.getSize());
        image.setHeight(Integer.parseInt(uploadResult.get("height").toString()));
        image.setWidth(Integer.parseInt(uploadResult.get("width").toString()));
        image.setFormat(uploadResult.get("format").toString());
        switch (imageType.toLowerCase()) {
            case "avatar", "profile picture", "profilepicture", "profile_picture" ->
                    image.setImageType(EImageType.PROFILE_PICTURE);
            case "cover photo", "coverphoto", "cover_photo" -> image.setImageType(EImageType.COVER_PHOTO);
            default -> image.setImageType(EImageType.POST_PHOTO);
        }
        imageRepository.save(image);
        return new MessageResponse("Tải ảnh thành công");

    }

    @Override
    public ImageDto getImageInfo(int imageId) {
        Optional<Image> image = imageRepository.findById(imageId);
        if (image.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy hình ảnh có Id " + imageId);
        } else {
            return responseUtils.getImageInfo(image.get());
        }
    }

    @Override
    public Page<ImageDto> getUserImageList(int userId, Integer page, Integer size) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy người dùng có Id " + userId);
        } else {
            List<Image> imageList = imageRepository.findByUser(user.get());
            List<ImageDto> imageDtoList = new ArrayList<>();
            for (Image result : imageList) {
                imageDtoList.add(responseUtils.getImageInfo(result));
            }
            imageDtoList = imageDtoList.stream().sorted(Comparator.comparing(ImageDto::getImageType)).collect(Collectors.toList());
            return responseUtils.pagingList(imageDtoList, page, size);
        }
    }

    @Override
    public ImageDto getUserProfilePicture(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy người dùng có Id " + userId);
        } else {
            List<Image> listProfilePicture = imageRepository.findByUser(user.get()).stream()
                    .filter(image -> image.getImageType() == EImageType.PROFILE_PICTURE)
                    .toList();
            Image currentProfilePicture = new Image();
            for (Image image : listProfilePicture) {
                currentProfilePicture = image;
            }
            return responseUtils.getImageInfo(currentProfilePicture);
        }
    }

    @Override
    public ImageDto getUserCoverPhoto(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy người dùng có Id " + userId);
        } else {
            List<Image> listProfilePicture = imageRepository.findByUser(user.get()).stream()
                    .filter(image -> image.getImageType() == EImageType.COVER_PHOTO)
                    .toList();
            Image currentProfilePicture = new Image();
            for (Image image : listProfilePicture) {
                currentProfilePicture = image;
            }
            return responseUtils.getImageInfo(currentProfilePicture);
        }
    }
}
