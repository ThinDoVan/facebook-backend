package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.PostRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.PostDto;
import com.example.facebookbackend.entities.Audience;
import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.PostVersion;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.EAudience;
import com.example.facebookbackend.repositories.*;
import com.example.facebookbackend.services.PostServices;
import com.example.facebookbackend.utils.AccessControlUtils;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServicesImpl implements PostServices {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostVersionRepository postVersionRepository;
    @Autowired
    AudienceRepository audienceRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    AccessControlUtils accessControlUtils;
    @Autowired
    ResponseUtils responseUtils;

    @Override
    public ResponseEntity<MessageResponse> createPost(User currentUser, PostRequest postRequest) {
        if (postRequest.getContent() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Bài viết thiếu nội dung"));
        }
        Post post = new Post();
        post.setCreatedUser(currentUser);
        post.setCreatedTime(LocalDateTime.now());
        if (postRequest.getAudience() == null) {
            post.setAudience(getAudience(EAudience.PUBLIC));
        } else {
            switch (postRequest.getAudience().toLowerCase()) {
                case "only me", "only_me", "onlyme" -> post.setAudience(getAudience(EAudience.ONLYME));
                case "friends" -> post.setAudience(getAudience(EAudience.FRIENDS));
                default -> post.setAudience(getAudience(EAudience.PUBLIC));
            }
        }
        postRepository.save(post);

        PostVersion postVersion = new PostVersion(post,
                postRequest.getContent(),
                post.getCreatedTime());
        postVersionRepository.save(postVersion);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Tạo bài viết thành công"));
    }


    @Override
    public ResponseEntity<?> getPost(User currentUser, int postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có Id: " + postId));
        } else {
            if (accessControlUtils.checkReadPermission(currentUser, post.get())) {
                return ResponseEntity.status(HttpStatus.OK).body(responseUtils.getPostInfo(post.get()));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền xem bài viết"));
            }
        }
    }


    @Override
    public ResponseEntity<?> getUserPostList(User currentUser, Integer userId, Integer page, Integer size) {
        Optional<User> authorUser = userRepository.findById(userId);
        if (authorUser.isEmpty() || !currentUser.isEnable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có Id: " + userId));
        } else {
            List<PostDto> postDtoList = new ArrayList<>();
            List<Post> postList = postRepository.findAllByCreatedUserAndAudience(authorUser.get(), getAudience(EAudience.PUBLIC));
            if ((accessControlUtils.isFriend(currentUser, authorUser.get())) || (accessControlUtils.isAdmin(currentUser)) || (currentUser.equals(authorUser.get()))) {
                postList.addAll(postRepository.findAllByCreatedUserAndAudience(authorUser.get(), getAudience(EAudience.FRIENDS)));
            }
            if ((currentUser.equals(authorUser.get())) || (accessControlUtils.isAdmin(currentUser))) {
                postList.addAll(postRepository.findAllByCreatedUserAndAudience(authorUser.get(), getAudience(EAudience.ONLYME)));
            }
            postList = postList.stream()
                    .filter(post -> !post.isDeleted())
                    .sorted(Comparator.comparing(Post::getPostId))
                    .collect(Collectors.toList());
            for (Post post : postList) {
                postDtoList.add(responseUtils.getPostInfo(post));
            }
            return ResponseEntity.status(HttpStatus.OK).body(responseUtils.pagingList(postDtoList, page, size));
        }
    }


    @Override
    public ResponseEntity<MessageResponse> updatePost(User currentUser, Integer postId, PostRequest
            postRequest) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có id: " + postId));
        } else {
            if (!accessControlUtils.checkEditPermission(currentUser, post.get().getCreatedUser())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền sửa bài viết này"));
            } else {
                if (postRequest.getAudience() != null) {
                    switch (postRequest.getAudience().toLowerCase()) {
                        case "only me", "only_me", "onlyme" -> post.get().setAudience(getAudience(EAudience.ONLYME));
                        case "friends" -> post.get().setAudience(getAudience(EAudience.FRIENDS));
                        default -> post.get().setAudience(getAudience(EAudience.PUBLIC));
                    }
                }
                if (postRequest.getContent() != null && !postRequest.getContent().isEmpty()) {
                    PostVersion postVersion = new PostVersion(post.get(),
                            postRequest.getContent(),
                            LocalDateTime.now());
                    postVersionRepository.save(postVersion);
                }
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Sửa bài viết thành công"));
            }
        }
    }


    @Override
    public ResponseEntity<MessageResponse> deletePost(User currentUser, Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có id: " + postId));
        } else {

            if (accessControlUtils.checkDeletePermission(currentUser, post.get().getCreatedUser())) {
                post.get().setDeleted(true);
                post.get().setDeletedUser(currentUser);
                post.get().setDeletedTime(LocalDateTime.now());
                postRepository.save(post.get());
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Xóa bài viết thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền xóa bài viết này"));
            }
        }
    }

    private Audience getAudience(EAudience eAudience) {
        return audienceRepository.findByAudienceType(eAudience).orElseThrow(() -> new RuntimeException("Không tìm thấy Audience"));
    }
}
