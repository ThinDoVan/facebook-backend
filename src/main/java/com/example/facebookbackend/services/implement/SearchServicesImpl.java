package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.PostDto;
import com.example.facebookbackend.dtos.response.UserDto;
import com.example.facebookbackend.entities.PostVersion;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.repositories.PostVersionRepository;
import com.example.facebookbackend.repositories.UserRepository;
import com.example.facebookbackend.services.SearchServices;
import com.example.facebookbackend.utils.AccessControlUtils;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServicesImpl implements SearchServices {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostVersionRepository postVersionRepository;

    @Autowired
    ResponseUtils responseUtils;
    @Autowired
    AccessControlUtils accessControlUtils;

    @Override
    public ResponseEntity<?> searchUser(User currentUser, String name, String relationship, String city, String school, String company, Integer page, Integer size) {
        List<User> userList = userRepository.findUserByFullNameContains(name).stream()
                .filter(User::isEnable)
                .filter(user -> {
                    if (relationship != null && !relationship.isEmpty()) {
                        if (relationship.equalsIgnoreCase("friends")) {
                            return accessControlUtils.isFriend(user, currentUser);
                        }
                    }
                    return true;
                })
                .filter(user -> city == null || city.isEmpty() || user.getCity().toLowerCase().contains(city.toLowerCase()))
                .filter(user -> school == null || school.isEmpty() || user.getSchool().toLowerCase().contains(school.toLowerCase()))
                .filter(user -> company == null || company.isEmpty() || user.getCompany().toLowerCase().contains(company.toLowerCase()))
                .toList();
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList) {
            userDtoList.add(responseUtils.getUserInfo(user));
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(responseUtils.pagingList(userDtoList, page, size));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Quá số lượng trang tối đa"));
        }
    }

    //Chưa kiểm tra quyền xem bài viết
    @Override
    public ResponseEntity<?> searchPost(User currentUser, String keyword, String postedBy, Integer postYear, Integer page, Integer size) {

        List<PostVersion> postVersionList = postVersionRepository.findByContentContains('%'+keyword+'%').stream()
                .filter(postVersion -> !postVersion.getPost().isDeleted())
                .filter(postVersion -> accessControlUtils.checkReadPermission(currentUser, postVersion.getPost()))
                .filter(postVersion -> {
                    if (postedBy != null && !postedBy.isEmpty()) {
                        switch (postedBy.toLowerCase()) {
                            case "me" -> {
                                return accessControlUtils.isAuthor(currentUser, postVersion.getPost().getCreatedUser());
                            }
                            case "friends" -> {
                                return accessControlUtils.isFriend(currentUser, postVersion.getPost().getCreatedUser());
                            }
                            default -> {
                                return true;
                            }
                        }
                    }else {
                        return true;
                    }
                })
                .filter(postVersion -> postYear == null || postYear == postVersion.getPost().getCreatedTime().getYear())
                .toList();
        List<PostDto> postDtoList = new ArrayList<>();
        for (PostVersion result:postVersionList) {
            postDtoList.add(responseUtils.getPostInfo(result.getPost()));
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(responseUtils.pagingList(postDtoList, page, size));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Quá số lượng trang tối đa"));
        }
    }
}
