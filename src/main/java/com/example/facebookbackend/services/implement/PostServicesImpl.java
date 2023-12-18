package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.PostRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.PostResponse;
import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.PostVersion;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.EAudience;
import com.example.facebookbackend.enums.ERole;
import com.example.facebookbackend.repositories.*;
import com.example.facebookbackend.services.PostServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    @Override
    public ResponseEntity<MessageResponse> createPost(String email, PostRequest postRequest) {
        if (postRequest.getContent() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Bài viết thiếu nội dung"));
        }
        Post post = new Post();
        post.setCreatedUser(userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tồn tại người dùng có email: " + email)));
        post.setCreatedTime(LocalDateTime.now());
        Set<PostVersion> postVersionSet = new HashSet<>();
        post.setPostVersionSet(postVersionSet);
        switch (postRequest.getAudience().toLowerCase()) {
            case "only me", "only_me", "onlyme" ->
                    post.setAudience(audienceRepository.findByAudienceType(EAudience.ONLYME)
                            .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đối tượng")));
            case "friends" -> post.setAudience(audienceRepository.findByAudienceType(EAudience.FRIENDS)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đối tượng")));
            default -> post.setAudience(audienceRepository.findByAudienceType(EAudience.PUBLIC)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đối tượng")));
        }
        postRepository.save(post);

        PostVersion postVersion = new PostVersion(post,
                postRequest.getContent(),
                post.getCreatedTime());
        postVersionRepository.save(postVersion);
        post.setPostVersionSet(postVersion);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Tạo bài viết thành công"));
    }

    @Override
    public ResponseEntity<?> getPost(int postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()|| post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có Id: " + postId));
        } else {
            PostVersion lastVersion = null;
            for (PostVersion version : post.get().getPostVersionSet()) {
                lastVersion = version;
            }
            PostResponse postResponse = new PostResponse(post.get().getPostId(),
                    lastVersion != null ? lastVersion.getContent() : null,
                    post.get().getCreatedTime(),
                    post.get().getCreatedUser(),
                    post.get().getAudience().getAudienceType());
            return ResponseEntity.status(HttpStatus.OK).body(postResponse);
        }
    }


    @Override
    public ResponseEntity<MessageResponse> updatePost(String email, Integer postId, PostRequest postRequest) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()|| post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tồn tại bài viết có id: " + postId));
        } else {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty() || !user.get().isEnable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tồn tại người dùng có email: " + email));
            } else {
                if (post.get().getCreatedUser() != user.get()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền sửa bài viết này"));
                } else {
                    if (postRequest.getAudience() != null) {
                        switch (postRequest.getAudience().toLowerCase()) {
                            case "only me", "only_me", "onlyme" ->
                                    post.get().setAudience(audienceRepository.findByAudienceType(EAudience.ONLYME)
                                            .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đối tượng")));
                            case "friends" ->
                                    post.get().setAudience(audienceRepository.findByAudienceType(EAudience.FRIENDS)
                                            .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đối tượng")));
                            default -> post.get().setAudience(audienceRepository.findByAudienceType(EAudience.PUBLIC)
                                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đối tượng")));
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
    }

    @Override
    public ResponseEntity<MessageResponse> deletePost(String email, Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()|| post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tồn tại bài viết có id: " + postId));
        } else {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty() || !user.get().isEnable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tồn tại người dùng có email: " + email));
            } else {
                if ((post.get().getCreatedUser()==user.get())||(user.get().getRoleSet().contains(roleRepository.findByRole(ERole.ROLE_ADMIN).orElseThrow(()-> new RuntimeException("Lỗi: Không tìm thấy Role"))))){
                    post.get().setDeleted(true);
                    post.get().setDeletedUser(user.get());
                    post.get().setDeletedTime(LocalDateTime.now());
                    postRepository.save(post.get());
                    return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Xóa bài viết thành công"));
                }else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền xóa bài viết này"));
                }
            }
        }
    }
}
