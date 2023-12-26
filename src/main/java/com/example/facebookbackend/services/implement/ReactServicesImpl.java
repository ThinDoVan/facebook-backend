package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.CommentRequest;
import com.example.facebookbackend.dtos.request.ReportRequestDto;
import com.example.facebookbackend.dtos.response.CommentDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.*;
import com.example.facebookbackend.enums.ERequestStatus;
import com.example.facebookbackend.repositories.*;
import com.example.facebookbackend.services.ReactServices;
import com.example.facebookbackend.utils.AccessControlUtils;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    ReportRequestRepository reportRequestRepository;

    @Autowired
    AccessControlUtils accessControlUtils;
    @Autowired
    ResponseUtils responseUtils;

    @Override
    public ResponseEntity<MessageResponse> likePost(User currentUser, Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có id: " + postId+" hoặc bài viết đã bị xóa"));
        } else {
            if (likePostRepository.findByPostAndUser(post.get(), currentUser).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("Bạn đã thích bài viết này trước đây"));
            } else {
                if (accessControlUtils.checkReadPermission(currentUser, post.get())) {
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có Id: " + commentRequest.getRepliedItemId()+" hoặc bài viết đã bị xóa"));
        } else {
            if (accessControlUtils.checkReadPermission(currentUser, post.get())) {
                if (commentRequest.getContent() == null || commentRequest.getContent().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Bình luận thiếu nội dung"));
                } else {
                    Comment comment = new Comment(post.get(),
                            currentUser,
                            LocalDateTime.now(),
                            null);
                    commentRepository.save(comment);

                    CommentVersion commentVersion = new CommentVersion(comment,
                            commentRequest.getContent(),
                            comment.getCreatedTime());
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bình luận có Id: " + commentId+" hoặc bình luận đã bị xóa"));
        } else {
            if (likeCommentRepository.findByCommentAndUser(comment.get(), currentUser).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("Bạn đã thích bình luận này trước đây"));
            } else {
                if (accessControlUtils.checkReadPermission(currentUser, comment.get().getPost())) {
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bình luận có Id: " + commentRequest.getRepliedItemId()+" hoặc bình luận đã bị xóa"));
        } else {
            if (accessControlUtils.checkReadPermission(currentUser, parentComment.get().getPost())) {
                if (commentRequest.getContent() == null || commentRequest.getContent().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Bình luận thiếu nội dung"));
                } else {
                    Comment comment = new Comment(parentComment.get().getPost(),
                            currentUser,
                            LocalDateTime.now(),
                            parentComment.get());
                    commentRepository.save(comment);

                    CommentVersion commentVersion = new CommentVersion(comment,
                            commentRequest.getContent(),
                            comment.getCreatedTime());
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bình luận có Id: " + commentRequest.getRepliedItemId()+" hoặc bình luận đã bị xóa"));
        } else {
            if (accessControlUtils.checkEditPermission(currentUser, comment.get().getCreatedUser())) {
                if (commentRequest.getContent() == null || commentRequest.getContent().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Bình luận thiếu nội dung"));
                } else {
                    CommentVersion commentVersion = new CommentVersion(comment.get(),
                            commentRequest.getContent(),
                            LocalDateTime.now()
                    );
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bình luận có Id: " + commentId+" hoặc bình luận đã bị xóa"));
        } else {
            if (accessControlUtils.checkDeletePermission(currentUser, comment.get().getCreatedUser())) {
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

    @Override
    public ResponseEntity<?> getPostComments(User currentUser, Integer postId, Integer page, Integer size) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có Id: " + postId+" hoặc bài viết đã bị xóa"));
        } else {
            if (accessControlUtils.checkReadPermission(currentUser, post.get())) {
                List<CommentDto> commentDtoList = new ArrayList<>();
                List<Comment> commentList = commentRepository.findByPost(post.get()).stream()
                        .filter(comment -> !comment.isDeleted())
                        .filter(comment -> comment.getParentComment() == null)
                        .toList();
                if (!commentList.isEmpty()) {
                    for (Comment comment : commentList) {
                        commentDtoList.add(responseUtils.getChildComment(comment));
                    }
                    return ResponseEntity.status(HttpStatus.OK).body(responseUtils.pagingList(commentDtoList, page, size));
                } else {
                    return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Bài viết vẫn chưa có bình luận"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền xem bình luận");
            }
        }
    }

    @Override
    public ResponseEntity<MessageResponse> reportPost(User currentUser, ReportRequestDto reportRequestDto) {
        Optional<Post> post = postRepository.findById(reportRequestDto.getId());
        if (post.isEmpty() || post.get().isDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có id: " + reportRequestDto.getId()+" hoặc bài viết đã bị xóa"));
        } else {
            if (accessControlUtils.checkReadPermission(currentUser, post.get())) {
                if (!reportRequestRepository.findByPostAndCreatedUser(post.get(), currentUser).isEmpty()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn đã gửi báo cáo về bài viết này trước đây"));
                } else {
                    ReportRequest request = new ReportRequest();
                    request.setPost(post.get());
                    request.setReason(reportRequestDto.getReason());
                    request.setCreatedUser(currentUser);
                    request.setCreatedTime(LocalDateTime.now());
                    request.setRequestStatus(ERequestStatus.PENDING);
                    reportRequestRepository.save(request);
                    return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Cảm ơn bạn đã gửi báo cáo bài viết. Chúng tôi sẽ xem xét và xử lý trường hợp này"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Bạn không có quyền xem bài viết"));
            }
        }
    }
}
