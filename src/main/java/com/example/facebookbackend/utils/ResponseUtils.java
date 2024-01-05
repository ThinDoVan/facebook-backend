package com.example.facebookbackend.utils;

import com.example.facebookbackend.dtos.response.*;
import com.example.facebookbackend.entities.*;
import com.example.facebookbackend.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseUtils {
    private final PostVersionRepository postVersionRepository;
    private final ImageRepository imageRepository;
    private final CommentVersionRepository commentVersionRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final MailSender mailSender;

    @Autowired
    public ResponseUtils(PostVersionRepository postVersionRepository,
                         ImageRepository imageRepository,
                         CommentVersionRepository commentVersionRepository,
                         CommentRepository commentRepository,
                         ModelMapper modelMapper,
                         MailSender mailSender) {
        this.postVersionRepository = postVersionRepository;
        this.imageRepository = imageRepository;
        this.commentVersionRepository = commentVersionRepository;
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.mailSender = mailSender;
    }

    public void sendEmail(User receivedUser, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receivedUser.getEmail());
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public <T> Page<T> pagingList(List<T> list, int page, int size) throws IllegalArgumentException {
        PageRequest pageRequest = PageRequest.of(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), list.size());
        if (end >= start) {
            List<T> pageData = list.subList(start, end);
            return new PageImpl<>(pageData, pageRequest, list.size());
        } else {
            throw new IllegalArgumentException("Vượt quá số trang tối đa");
        }
    }

    public FriendRequestDto getFriendRequestInfo(FriendRequest friendRequest) {
        return friendRequest != null ? modelMapper.map(friendRequest, FriendRequestDto.class) : null;
    }

    public UserDto getUserInfo(User user) {
        return user != null ? modelMapper.map(user, UserDto.class) : null;
    }

    public ImageDto getImageInfo(Image image) {
        return new ImageDto(this.getUserInfo(image.getUser()),
                image.getCreatedTime(),
                image.getFileName(),
                image.getContentType(),
                image.getUrl(),
                image.getSize(),
                image.getImageType());
    }

//    public ReportReqDto getReportRequestInfo(ReportRequest reportRequest) {
//        return new ReportReqDto(reportRequest.getReportRequestId(),
//                reportRequest.getPost() != null ? this.getPostInfo(reportRequest.getPost()) : null,
//                reportRequest.getUser() != null ? this.getUserInfo(reportRequest.getUser()) : null,
//                this.getUserInfo(reportRequest.getCreatedUser()),
//                reportRequest.getCreatedTime(),
//                reportRequest.getReason(),
//                this.getUserInfo(reportRequest.getAdmin()),
//                reportRequest.getProcessedTime(),
//                reportRequest.getRequestStatus(),
//                reportRequest.getAction());
//    }
    public ReportReqDto getReportUserInfo(ReportUser reportUser){
        return new ReportReqDto(reportUser.getReportRequestId(),
                this.getUserInfo(reportUser.getUser()),
                this.getUserInfo(reportUser.getCreatedUser()),
                reportUser.getCreatedTime(),
                reportUser.getReason(),
                this.getUserInfo(reportUser.getAdmin()),
                reportUser.getProcessedTime(),
                reportUser.getRequestStatus(),
                reportUser.getAction());
    }
    public ReportReqDto getReportPostInfo(ReportPost reportPost){
        return new ReportReqDto(reportPost.getReportRequestId(),
                this.getPostInfo(reportPost.getPost()),
                this.getUserInfo(reportPost.getCreatedUser()),
                reportPost.getCreatedTime(),
                reportPost.getReason(),
                this.getUserInfo(reportPost.getAdmin()),
                reportPost.getProcessedTime(),
                reportPost.getRequestStatus(),
                reportPost.getAction());
    }

    private CommentVersion getLastCommentVersion(Comment comment) {
        List<CommentVersion> commentVersionList = commentVersionRepository.findByComment(comment);
        CommentVersion lastVersion = new CommentVersion();
        for (CommentVersion version : commentVersionList) {
            lastVersion = version;
        }
        return lastVersion;
    }

    public PostDto getPostInfo(Post post) {
        List<PostVersion> postVersionList = postVersionRepository.findAllByPost(post);
        PostVersion lastVersion = new PostVersion();
        for (PostVersion version : postVersionList) {
            lastVersion = version;
        }
        List<ImageDto> imageDtoList = new ArrayList<>();
        for (Image image : imageRepository.findByPostVersion(lastVersion)) {
            imageDtoList.add(this.getImageInfo(image));
        }
        PostDto postDto = new PostDto();
        postDto.setImageDtoList(imageDtoList);
        postDto.setPostId(post.getPostId());
        postDto.setContent(lastVersion.getContent());
        postDto.setCreatedUser(this.getUserInfo(post.getCreatedUser()));
        postDto.setCreatedDate(post.getCreatedTime());
        postDto.setAudience(post.getAudience().getAudienceType());
        postDto.setCountLike(post.getCountLike());
        postDto.setCountComment(post.getCountComment());
        return postDto;
    }

    public CommentDto getParrentComment(Comment comment) {
        CommentVersion lastVersion = getLastCommentVersion(comment);
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId(comment.getCommentId());
        commentDto.setCreatedUser(this.getUserInfo(comment.getCreatedUser()));
        commentDto.setContent(lastVersion.getContent());
        commentDto.setCreatedTime(lastVersion.getModifiedTime());
        commentDto.setCountLikes(comment.getCountLike());
//        commentDto.setCountReply(comment.getCountComment());
//        if (comment.getParentComment() != null) {
//            commentDto.setParrentComment(getParrentComment(comment.getParentComment()));
//        } else {
//            commentDto.setParrentComment(null);
//        }
        return commentDto;
    }

    public CommentDto getChildComment(Comment comment) {
        CommentVersion lastVersion = getLastCommentVersion(comment);
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId(comment.getCommentId());
        commentDto.setCreatedUser(this.getUserInfo(comment.getCreatedUser()));
        commentDto.setContent(lastVersion.getContent());
        commentDto.setCreatedTime(lastVersion.getModifiedTime());
        commentDto.setCountLikes(comment.getCountLike());
        List<Comment> commentList = commentRepository.findByParentComment(comment);
        List<CommentDto> childCommentList = new ArrayList<>();
        if (!commentList.isEmpty()) {
            for (Comment childComment : commentList) {
                childCommentList.add(getChildComment(childComment));
            }
        }
        commentDto.setChildComment(childCommentList);
        return commentDto;
    }
//
//    public int countLike(Post post) {
//        return likePostRepository.findByPost(post).size();
//    }
//
//    public int countLike(Comment comment) {
//        return likeCommentRepository.findByComment(comment).size();
//    }
//
//    public int countComments(Post post) {
//        List<Comment> commentList = commentRepository.findByPost(post).stream()
//                .filter((Comment comment) -> !comment.isDeleted()).toList();
//        return commentList.size();
//    }
//
//    public int countReply(Comment comment) {
//        List<Comment> commentList = commentRepository.findByParentComment(comment).stream()
//                .filter((Comment childComment) -> !childComment.isDeleted()).toList();
//        return commentList.size();
//    }
}
