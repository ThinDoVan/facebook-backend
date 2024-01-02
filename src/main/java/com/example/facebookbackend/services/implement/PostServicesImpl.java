package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.PostRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.PostDto;
import com.example.facebookbackend.entities.Audience;
import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.PostVersion;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.EAudience;
import com.example.facebookbackend.exceptions.DataNotFoundException;
import com.example.facebookbackend.exceptions.InvalidDataException;
import com.example.facebookbackend.exceptions.NotAllowedException;
import com.example.facebookbackend.repositories.*;
import com.example.facebookbackend.services.PostServices;
import com.example.facebookbackend.utils.AccessControlUtils;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public MessageResponse createPost(User currentUser, PostRequest postRequest) {
        if (postRequest.getContent() == null) {
            throw new InvalidDataException("Bài viết thiếu nội dung");
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

        return new MessageResponse("Tạo bài viết thành công");
    }

    @Override
    public PostDto getPost(User currentUser, int postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bài viết có id: " + postId + " hoặc bài viết đã bị xóa");
        } else {
            if (accessControlUtils.checkReadPermission(currentUser, post.get())) {
                return responseUtils.getPostInfo(post.get());
            } else {
                throw new NotAllowedException("Bạn không có quyền xem bài viết");
            }
        }
    }

    @Override
    public Page<PostDto> getUserPostList(User currentUser, Integer userId, Integer page, Integer size) {
        Optional<User> authorUser = userRepository.findById(userId);
        if (authorUser.isEmpty() || !currentUser.isEnable()) {
            throw new DataNotFoundException("Không tìm thấy người dùng có Id: " + userId);
        } else {
            List<PostDto> postDtoList = new ArrayList<>();
            List<Post> postList = postRepository.findAllByCreatedUser(authorUser.get());
            postList = postList.stream()
                    .filter(post -> !post.isDeleted())
                    .filter(post -> accessControlUtils.checkReadPermission(currentUser, post))
                    .sorted(Comparator.comparing(Post::getPostId))
                    .collect(Collectors.toList());
            for (Post post : postList) {
                postDtoList.add(responseUtils.getPostInfo(post));
            }
            try {
                return responseUtils.pagingList(postDtoList, page, size);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Quá số lượng trang tối đa");
            }
        }
    }

    @Override
    public MessageResponse updatePost(User currentUser, Integer postId, PostRequest
            postRequest) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bài viết có id: " + postId + " hoặc bài viết đã bị xóa");
        } else {
            if (!accessControlUtils.checkEditPermission(currentUser, post.get().getCreatedUser())) {
                throw new NotAllowedException("Bạn không có quyền sửa bài viết này");
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
                return new MessageResponse("Sửa bài viết thành công");
            }
        }
    }

    @Override
    public MessageResponse deletePost(User currentUser, Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bài viết có id: " + postId + " hoặc bài viết đã bị xóa");
        } else {
            if (accessControlUtils.checkDeletePermission(currentUser, post.get().getCreatedUser())) {
                post.get().setDeleted(true);
                post.get().setDeletedUser(currentUser);
                post.get().setDeletedTime(LocalDateTime.now());
                postRepository.save(post.get());
                return new MessageResponse("Xóa bài viết thành công");
            } else {
                throw new NotAllowedException("Bạn không có quyền xóa bài viết này");
            }
        }
    }

    private Audience getAudience(EAudience eAudience) {
        return audienceRepository.findByAudienceType(eAudience).orElseThrow(() -> new RuntimeException("Không tìm thấy Audience"));
    }
}