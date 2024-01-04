package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.CommentRequest;
import com.example.facebookbackend.dtos.request.ReportRequestDto;
import com.example.facebookbackend.dtos.response.CommentDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.*;
import com.example.facebookbackend.enums.ERequestStatus;
import com.example.facebookbackend.exceptions.DataNotFoundException;
import com.example.facebookbackend.exceptions.InvalidDataException;
import com.example.facebookbackend.exceptions.NotAllowedException;
import com.example.facebookbackend.repositories.*;
import com.example.facebookbackend.services.ReactServices;
import com.example.facebookbackend.utils.AccessControlUtils;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public MessageResponse likePost(User currentUser, Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bài viết có id: " + postId + " hoặc bài viết đã bị xóa");
        } else {
            if (!accessControlUtils.checkReadPermission(currentUser, post.get())) {
                throw new NotAllowedException("Bạn không có quyền thích bài viết này");
            } else {
                if (likePostRepository.findByPostAndUser(post.get(), currentUser).isPresent()) {
                    return new MessageResponse("Bạn đã thích bài viết này trước đây");
                } else {
                    LikePost likePost = new LikePost();
                    likePost.setPost(post.get());
                    likePost.setUser(currentUser);
                    likePost.setCreatedTime(LocalDateTime.now());
                    likePostRepository.save(likePost);

                    post.get().setCountLike(post.get().getCountLike()+1);
                    postRepository.save(post.get());
                    return new MessageResponse("Bạn đã thích bài viết");
                }
            }
        }
    }

    @Override
    public MessageResponse commentPost(User currentUser, CommentRequest commentRequest) {
        Optional<Post> post = postRepository.findById(commentRequest.getRepliedItemId());
        if (post.isEmpty() || post.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bài viết có Id: " + commentRequest.getRepliedItemId() + " hoặc bài viết đã bị xóa");
        } else {
            if (!accessControlUtils.checkReadPermission(currentUser, post.get())) {
                throw new NotAllowedException("Bạn không có quyền bình luận bài viết này");
            } else {
                if (commentRequest.getContent() == null || commentRequest.getContent().isEmpty()) {
                    throw new InvalidDataException("Bình luận thiếu nội dung");
                } else {
                    Comment comment = new Comment(post.get(),
                            currentUser,
                            LocalDateTime.now(),
                            null);
//                    comment.setCountLike(0);
                    commentRepository.save(comment);
//                    System.out.println("!!!!!!"+post.get().getCountComment());
                    CommentVersion commentVersion = new CommentVersion(comment,
                            commentRequest.getContent(),
                            comment.getCreatedTime());
                    commentVersionRepository.save(commentVersion);

                    post.get().setCountComment(post.get().getCountComment()+1);
                    postRepository.save(post.get());
                    return new MessageResponse("Bạn đã bình luận bài viết");
                }
            }
        }
    }

    @Override
    public MessageResponse likeComment(User currentUser, Integer commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty() || comment.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bình luận có Id: " + commentId + " hoặc bình luận đã bị xóa");
        } else {
            if (!accessControlUtils.checkReadPermission(currentUser, comment.get().getPost())) {
                throw new NotAllowedException("Bạn không có quyền thích bình luận này");
            } else {
                if (likeCommentRepository.findByCommentAndUser(comment.get(), currentUser).isPresent()) {
                    return new MessageResponse("Bạn đã thích bình luận này trước đây");
                } else {
                    LikeComment likeComment = new LikeComment();
                    likeComment.setComment(comment.get());
                    likeComment.setUser(currentUser);
                    likeComment.setCreatedTime(LocalDateTime.now());
                    likeCommentRepository.save(likeComment);

                    comment.get().setCountLike(comment.get().getCountLike()+1);
                    commentRepository.save(comment.get());
                    return new MessageResponse("Bạn đã thích bình luận");
                }
            }
        }
    }

    @Override
    public MessageResponse replyComment(User currentUser, CommentRequest commentRequest) {
        Optional<Comment> parentComment = commentRepository.findById(commentRequest.getRepliedItemId());
        if (parentComment.isEmpty() || parentComment.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bình luận có Id: " + commentRequest.getRepliedItemId() + " hoặc bình luận đã bị xóa");
        } else {
            Post post = parentComment.get().getPost();
            if (!accessControlUtils.checkReadPermission(currentUser, post)) {
                throw new NotAllowedException("Bạn không có quyền trả lời bình luận này");
            } else {
                if (commentRequest.getContent() == null || commentRequest.getContent().isEmpty()) {
                    throw new InvalidDataException("Bình luận thiếu nội dung");
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

                    post.setCountComment(post.getCountComment()+1);
                    postRepository.save(post);
                    return new MessageResponse("Bạn đã trả lời bình luận");
                }
            }
        }
    }

    @Override
    public MessageResponse updateComment(User currentUser, CommentRequest commentRequest) {
        Optional<Comment> comment = commentRepository.findById(commentRequest.getRepliedItemId());
        if (comment.isEmpty() || comment.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bình luận có Id: " + commentRequest.getRepliedItemId() + " hoặc bình luận đã bị xóa");
        } else {
            if (accessControlUtils.checkEditPermission(currentUser, comment.get().getCreatedUser())) {
                if (commentRequest.getContent() == null || commentRequest.getContent().isEmpty()) {
                    throw new InvalidDataException("Bình luận thiếu nội dung");
                } else {
                    CommentVersion commentVersion = new CommentVersion(comment.get(),
                            commentRequest.getContent(),
                            LocalDateTime.now()
                    );
                    commentVersionRepository.save(commentVersion);
                    return new MessageResponse("Sửa bình luận thành công");
                }
            } else {
                throw new NotAllowedException("Bạn không có quyền sửa bình luận này");
            }
        }
    }

    @Override
    public MessageResponse deleteComment(User currentUser, Integer commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty() || comment.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bình luận có Id: " + commentId + " hoặc bình luận đã bị xóa");
        } else {
            if (accessControlUtils.checkDeletePermission(currentUser, comment.get().getCreatedUser())) {
                comment.get().setDeleted(true);
                comment.get().setDeletedTime(LocalDateTime.now());
                comment.get().setDeletedUser(currentUser);
                commentRepository.save(comment.get());

                Post post = comment.get().getPost();
                post.setCountComment(post.getCountComment()-1);
                postRepository.save(post);
                return new MessageResponse("Xóa bình luận thành công");
            } else {
                throw new NotAllowedException("Bạn không có quyền xóa bình luận này");
            }
        }
    }

    @Override
    public Page<CommentDto> getPostComments(User currentUser, Integer postId, Integer page, Integer size) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bài viết có Id: " + postId + " hoặc bài viết đã bị xóa");
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
                    return responseUtils.pagingList(commentDtoList, page, size);
                } else {
                    throw new DataNotFoundException("Bài viết vẫn chưa có bình luận");
                }
            } else {
                throw new NotAllowedException("Bạn không có quyền xem bình luận");
            }
        }
    }
}