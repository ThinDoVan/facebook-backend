package com.example.facebookbackend.services.implement;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.facebookbackend.dtos.response.ImageDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.Image;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.EImageType;
import com.example.facebookbackend.repositories.ImageRepository;
import com.example.facebookbackend.repositories.UserRepository;
import com.example.facebookbackend.services.ImageServices;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

//    @Override
//    public ResponseEntity<MessageResponse> uploadImage(User currentUser, MultipartFile file, String imageType) throws IOException {
//        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Chỉ cho phép tải hình ảnh lên"));
//        } else {
//            Image image = new Image();
//            image.setUser(currentUser);
//            image.setCreatedTime(LocalDateTime.now());
//            String uploadDir;
//            switch (imageType.toLowerCase()) {
//                case "avatar", "profile picture", "profilepicture", "profile_picture" -> {
//                    image.setImageType(EImageType.PROFILE_PICTURE);
//                    uploadDir = "img/ProfilePicture/" + currentUser.getUserId();
//                }
//                case "cover photo", "coverphoto", "cover_photo" -> {
//                    image.setImageType(EImageType.COVER_PHOTO);
//                    uploadDir = "img/CoverPhoto/" + currentUser.getUserId();
//                }
//                default -> {
//                    image.setImageType(EImageType.POST_PHOTO);
//                    uploadDir = "img/PostPhoto/" + currentUser.getUserId();
//                }
//            }
//            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//            File newFile = new File(uploadDir, fileName);
//            if (!newFile.exists()) {
//                newFile.getParentFile().mkdirs();
//                newFile.createNewFile();
//
//            }
//            InputStream inputStream = file.getInputStream();
//            OutputStream outputStream = new FileOutputStream(newFile);
//            byte[] buffer = new byte[2048];
//            int length;
//            while ((length = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, length);
//            }
//            inputStream.close();
//            outputStream.close();
//            image.setUrl(newFile.getPath());
//            imageRepository.save(image);
//            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Tải ảnh thành công"));
//        }
//    }

    @Override
    public ResponseEntity<MessageResponse> uploadImage(User currentUser, MultipartFile multipartFile, String imageType) {
        try {
            Map uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.emptyMap());
            System.out.println(uploadResult);
            Image image = new Image();
            image.setUser(currentUser);
            image.setCreatedTime(LocalDateTime.now());
            image.setUrl(uploadResult.get("url").toString());
            image.setFileName(multipartFile.getOriginalFilename());
            image.setContentType(multipartFile.getContentType());
            image.setSize(multipartFile.getSize());
            switch (imageType.toLowerCase()) {
                case "avatar", "profile picture", "profilepicture", "profile_picture" ->
                        image.setImageType(EImageType.PROFILE_PICTURE);
                case "cover photo", "coverphoto", "cover_photo" -> image.setImageType(EImageType.COVER_PHOTO);
                default -> image.setImageType(EImageType.POST_PHOTO);
            }
            imageRepository.save(image);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Tải ảnh thành công"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Tải ảnh thất bại"));
        }
    }

    @Override
    public ResponseEntity<?> getImageInfo(int imageId) {
        Optional<Image> image = imageRepository.findById(imageId);
        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy hình ảnh có Id " + imageId));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(responseUtils.getImageInfo(image.get()));
        }
    }

    @Override
    public ResponseEntity<?> getUserImageList(int userId, Integer page, Integer size) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có Id " + userId));
        } else {
            List<Image> imageList = imageRepository.findByUser(user.get());
            List<ImageDto> imageDtoList = new ArrayList<>();
            for (Image result : imageList) {
                imageDtoList.add(responseUtils.getImageInfo(result));
            }
            imageDtoList = imageDtoList.stream().sorted(Comparator.comparing(ImageDto::getImageType)).collect(Collectors.toList());
            try {
                return ResponseEntity.status(HttpStatus.OK).body(responseUtils.pagingList(imageDtoList, page, size));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Quá số lượng trang tối đa"));
            }
        }
    }

    @Override
    public ResponseEntity<?> getUserProfilePicture(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có Id " + userId));
        } else {
            List<Image> listProfilePicture = imageRepository.findByUser(user.get()).stream()
                    .filter(image -> image.getImageType() == EImageType.PROFILE_PICTURE)
                    .toList();
            Image currentProfilePicture = new Image();
            for (Image image : listProfilePicture) {
                currentProfilePicture = image;
            }
            return ResponseEntity.status(HttpStatus.OK).body(responseUtils.getImageInfo(currentProfilePicture));
        }
    }

    @Override
    public ResponseEntity<?> getUserCoverPhoto(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có Id " + userId));
        } else {
            List<Image> listProfilePicture = imageRepository.findByUser(user.get()).stream()
                    .filter(image -> image.getImageType() == EImageType.COVER_PHOTO)
                    .toList();
            Image currentProfilePicture = new Image();
            for (Image image : listProfilePicture) {
                currentProfilePicture = image;
            }
            return ResponseEntity.status(HttpStatus.OK).body(responseUtils.getImageInfo(currentProfilePicture));
        }
    }
}
