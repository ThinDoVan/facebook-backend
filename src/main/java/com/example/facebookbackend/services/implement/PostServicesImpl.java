package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.PostRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.PostDto;
import com.example.facebookbackend.dtos.response.UserDto;
import com.example.facebookbackend.entities.Audience;
import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.PostVersion;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.EAudience;
import com.example.facebookbackend.enums.ERole;
import com.example.facebookbackend.helper.ResponseUtils;
import com.example.facebookbackend.repositories.*;
import com.example.facebookbackend.services.PostServices;
import org.modelmapper.ModelMapper;
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
    ModelMapper modelMapper;
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

    @Override
    public ResponseEntity<MessageResponse> createPost(String currentUserEmail, PostRequest postRequest) {
        if (postRequest.getContent() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Bài viết thiếu nội dung"));
        }
        Optional<User> user = userRepository.findByEmail(currentUserEmail);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có email: " + currentUserEmail));
        } else {
            Post post = new Post();
            post.setCreatedUser(user.get());
            post.setCreatedTime(LocalDateTime.now());

            switch (postRequest.getAudience().toLowerCase()) {
                case "only me", "only_me", "onlyme" -> post.setAudience(getAudience(EAudience.ONLYME));
                case "friends" -> post.setAudience(getAudience(EAudience.FRIENDS));
                default -> post.setAudience(getAudience(EAudience.PUBLIC));
            }
            postRepository.save(post);

            PostVersion postVersion = new PostVersion(post,
                    postRequest.getContent(),
                    post.getCreatedTime());
            postVersionRepository.save(postVersion);

            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Tạo bài viết thành công"));
        }
    }

    @Override
    public ResponseEntity<?> getPost(String currentUserEmail, int postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có Id: " + postId));
        } else {
            Optional<User> currentUser = userRepository.findByEmail(currentUserEmail);
            if (currentUser.isEmpty() || !currentUser.get().isEnable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có email: " + currentUserEmail));
            } else {
                if (checkReadPermission(currentUser.get(), post.get())) {
                    return ResponseEntity.status(HttpStatus.OK).body(this.getPostInfo(post.get()));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền xem bài viết"));
                }
            }
        }
    }

    @Override
    public ResponseEntity<?> getUserPostList(String currentUserEmail, Integer userId, Integer page, Integer size) {
        Optional<User> currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser.isEmpty() || !currentUser.get().isEnable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có email: " + currentUserEmail));
        } else {
            Optional<User> authorUser = userRepository.findById(userId);
            if (authorUser.isEmpty() || !currentUser.get().isEnable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có Id: " + userId));
            } else {
                List<PostDto> postDtoList = new ArrayList<>();
                List<Post> postList = postRepository.findAllByCreatedUserAndAudience(authorUser.get(), getAudience(EAudience.PUBLIC));
                if ((isFriend(currentUser.get(), authorUser.get())) || (isAdmin(currentUser.get())) || (currentUser.get() == authorUser.get())) {
                    postList.addAll(postRepository.findAllByCreatedUserAndAudience(authorUser.get(), getAudience(EAudience.FRIENDS)));
                }
                if ((currentUser.get() == authorUser.get()) || (isAdmin(currentUser.get()))) {
                    postList.addAll(postRepository.findAllByCreatedUserAndAudience(authorUser.get(), getAudience(EAudience.ONLYME)));
                }
                postList = postList.stream()
                        .filter(post -> !post.isDeleted())
                        .sorted(Comparator.comparing(Post::getPostId))
                        .collect(Collectors.toList());
                for (Post post : postList) {
                    postDtoList.add(this.getPostInfo(post));
                }
                return ResponseEntity.status(HttpStatus.OK).body(ResponseUtils.pagingList(postDtoList, page, size));
            }
        }
    }


    @Override
    public ResponseEntity<MessageResponse> updatePost(String currentUserEmail, Integer postId, PostRequest
            postRequest) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có id: " + postId));
        } else {
            Optional<User> currentUser = userRepository.findByEmail(currentUserEmail);
            if (currentUser.isEmpty() || !currentUser.get().isEnable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có email: " + currentUserEmail));
            } else {
                if (!checkEditPermission(currentUser.get(), post.get())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền sửa bài viết này"));
                } else {
                    if (postRequest.getAudience() != null) {
                        switch (postRequest.getAudience().toLowerCase()) {
                            case "only me", "only_me", "onlyme" ->
                                    post.get().setAudience(getAudience(EAudience.ONLYME));
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
    }

    @Override
    public ResponseEntity<MessageResponse> deletePost(String currentUserEmail, Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có id: " + postId));
        } else {
            Optional<User> currentUser = userRepository.findByEmail(currentUserEmail);
            if (currentUser.isEmpty() || !currentUser.get().isEnable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có email: " + currentUserEmail));
            } else {
                if (checkDeletePermission(currentUser.get(), post.get())) {
                    post.get().setDeleted(true);
                    post.get().setDeletedUser(currentUser.get());
                    post.get().setDeletedTime(LocalDateTime.now());
                    postRepository.save(post.get());
                    return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Xóa bài viết thành công"));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền xóa bài viết này"));
                }
            }
        }
    }

    private PostDto getPostInfo(Post post) {
        List<PostVersion> postVersionList = postVersionRepository.findAllByPost(post);
        PostVersion lastVersion = new PostVersion();
        for (PostVersion version : postVersionList) {
            lastVersion = version;
        }
        return new PostDto(post.getPostId(),
                lastVersion.getContent(),
                post.getCreatedTime(),
                modelMapper.map(post.getCreatedUser(), UserDto.class),
                post.getAudience().getAudienceType());
    }

    private boolean isFriend(User currentUser, User authorUser) {
        return (friendshipRepository.findByUser1AndUser2(currentUser, authorUser) != null)
                || (friendshipRepository.findByUser1AndUser2(authorUser, currentUser) != null);
    }

    private boolean isAdmin(User currentUser) {
        return currentUser.getRoleSet().contains(roleRepository.findByRole(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role")));
    }

    private boolean checkEditPermission(User currentUser, Post post) {
        return currentUser == post.getCreatedUser();
    }

    private boolean checkDeletePermission(User currentUser, Post post) {
        return currentUser == post.getCreatedUser()
                || isAdmin(currentUser);
    }

    private boolean checkReadPermission(User currentUser, Post post) {
        if ((currentUser == post.getCreatedUser())
                || (isAdmin(currentUser))) {
            return true;
        } else {
            switch (post.getAudience().getAudienceType()) {
                case ONLYME -> {
                    return false;
                }
                case FRIENDS -> {
                    return isFriend(currentUser, post.getCreatedUser());
                }
                default -> {
                    return true;
                }
            }
        }
    }

    private Audience getAudience(EAudience eAudience) {
        return audienceRepository.findByAudienceType(eAudience).orElseThrow(() -> new RuntimeException("Không tìm thấy Audience"));
    }
}
