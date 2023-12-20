package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.CommentRequest;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.*;
import com.example.facebookbackend.enums.ERole;
import com.example.facebookbackend.repositories.*;
import com.example.facebookbackend.services.ReactServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReactServicesImpl implements ReactServices {
    @Autowired
    UserRepository userRepository;
    @Autowired
    LikePostRepository likePostRepository;
    @Autowired
    LikeCommentRepository likeCommentRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    FriendshipRepository friendshipRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CommentVersionRepository commentVersionRepository;

    //Kiểm tra đã like trước đó hay chưa
    @Override
    public ResponseEntity<MessageResponse> likePost(User currentUser, Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có Id: " + postId));
        } else {
            if (likePostRepository.findByPostAndUser(post.get(), currentUser).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("Bạn đã thích bài viết này trước đây"));
            } else {
                if (checkReadPermission(currentUser, post.get())) {
                    LikePost likePost = new LikePost();
                    likePost.setPost(post.get());
                    likePost.setUser(currentUser);
                    likePost.setCreatedTime(LocalDateTime.now());
                    likePostRepository.save(likePost);
                    return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Bạn đã thích bài viết"));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền thích bài viết này"));
                }
            }
        }
    }

    @Override
    public ResponseEntity<MessageResponse> commentPost(User currentUser, CommentRequest commentRequest) {
        Optional<Post> post = postRepository.findById(commentRequest.getRepliedItemId());
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có Id: " + commentRequest.getRepliedItemId()));
        } else {
            if (checkReadPermission(currentUser, post.get())) {
                if (commentRequest.getContent() == null || commentRequest.getContent().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Bình luận thiếu nội dung"));
                } else {
                    Comment comment = new Comment();
                    comment.setPost(post.get());
                    comment.setParentComment(null);
                    comment.setCreatedUser(currentUser);
                    comment.setCreatedTime(LocalDateTime.now());
                    commentRepository.save(comment);

                    CommentVersion commentVersion = new CommentVersion();
                    commentVersion.setComment(comment);
                    commentVersion.setContent(commentRequest.getContent());
                    commentVersion.setModifiedTime(comment.getCreatedTime());
                    commentVersionRepository.save(commentVersion);
                    return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Bạn đã bình luận bài viết"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền bình luận bài viết này"));
            }
        }
    }

    @Override
    public ResponseEntity<MessageResponse> likeComment(User currentUser, Integer commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty() || comment.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bình luận có Id: " + commentId));
        } else {
            if (likeCommentRepository.findByCommentAndUser(comment.get(), currentUser).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("Bạn đã thích bình luận này trước đây"));
            } else {
                if (checkReadPermission(currentUser, comment.get().getPost())) {
                    LikeComment likeComment = new LikeComment();
                    likeComment.setComment(comment.get());
                    likeComment.setUser(currentUser);
                    likeComment.setCreatedTime(LocalDateTime.now());
                    likeCommentRepository.save(likeComment);
                    return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Bạn đã thích bình luận"));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền thích bình luận này"));
                }
            }
        }
    }

    @Override
    public ResponseEntity<MessageResponse> replyComment(User currentUser, CommentRequest commentRequest) {
        Optional<Comment> parentComment = commentRepository.findById(commentRequest.getRepliedItemId());
        if (parentComment.isEmpty() || parentComment.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bình luận có Id: " + commentRequest.getRepliedItemId()));
        } else {
            if (checkReadPermission(currentUser, parentComment.get().getPost())) {
                if (commentRequest.getContent() == null || commentRequest.getContent().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Bình luận thiếu nội dung"));
                } else {
                    Comment comment = new Comment();
                    comment.setPost(parentComment.get().getPost());
                    comment.setParentComment(parentComment.get());
                    comment.setCreatedUser(currentUser);
                    comment.setCreatedTime(LocalDateTime.now());
                    commentRepository.save(comment);

                    CommentVersion commentVersion = new CommentVersion();
                    commentVersion.setComment(comment);
                    commentVersion.setContent(commentRequest.getContent());
                    commentVersion.setModifiedTime(comment.getCreatedTime());
                    commentVersionRepository.save(commentVersion);

                    return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Bạn đã trả lời bình luận"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền trả lời bình luận này"));
            }
        }
    }

    @Override
    public ResponseEntity<MessageResponse> updateComment(User currentUser, CommentRequest commentRequest) {
        Optional<Comment> comment = commentRepository.findById(commentRequest.getRepliedItemId());
        if (comment.isEmpty() || comment.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bình luận có Id: " + commentRequest.getRepliedItemId()));
        } else {
            if (checkEditPermission(currentUser, comment.get().getCreatedUser())) {
                if (commentRequest.getContent() == null || commentRequest.getContent().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Bình luận thiếu nội dung"));
                } else {
                    CommentVersion commentVersion = new CommentVersion();
                    commentVersion.setComment(comment.get());
                    commentVersion.setContent(commentRequest.getContent());
                    commentVersion.setModifiedTime(comment.get().getCreatedTime());
                    commentVersionRepository.save(commentVersion);
                    return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Sửa bình luận thành công"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền sửa bình luận này"));
            }
        }
    }

    @Override
    public ResponseEntity<MessageResponse> deleteComment(User currentUser, Integer commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty() || comment.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bình luận có Id: " + commentId));
        } else {
            if (checkDeletePermission(currentUser, comment.get().getCreatedUser())) {
                comment.get().setDeleted(true);
                comment.get().setDeletedTime(LocalDateTime.now());
                comment.get().setDeletedUser(currentUser);
                commentRepository.save(comment.get());
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Xóa bình luận thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền xóa bình luận này"));
            }
        }
    }

    private boolean isFriend(User user, User authorUser) {
        return (friendshipRepository.findByUser1AndUser2(user, authorUser) != null)
                || (friendshipRepository.findByUser1AndUser2(authorUser, user) != null);
    }

    private boolean isAdmin(User user) {
        return user.getRoleSet().contains(roleRepository.findByRole(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role")));
    }

    private boolean isAuthor(User user, User author){
        return user.equals(author);
    }
    private boolean checkEditPermission(User user, User author) {
        return isAuthor(user, author);
    }

    private boolean checkDeletePermission(User user, User author) {
        return isAuthor(user, author)
                || isAdmin(user);
    }

    private boolean checkReadPermission(User user, Post post) {
        if ((user.equals(post.getCreatedUser()))
                || (isAdmin(user))) {
            return true;
        } else {
            switch (post.getAudience().getAudienceType()) {
                case ONLYME -> {
                    return false;
                }
                case FRIENDS -> {
                    return isFriend(user, post.getCreatedUser());
                }
                default -> {
                    return true;
                }
            }
        }
    }

}
