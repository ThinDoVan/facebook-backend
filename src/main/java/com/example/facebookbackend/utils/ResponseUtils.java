package com.example.facebookbackend.utils;

import com.example.facebookbackend.dtos.response.*;
import com.example.facebookbackend.entities.*;
import com.example.facebookbackend.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseUtils {
    private final PostVersionRepository postVersionRepository;
    private final CommentVersionRepository commentVersionRepository;
    private final CommentRepository commentRepository;
    private final LikePostRepository likePostRepository;
    private final LikeCommentRepository likeCommentRepository;
    private final ModelMapper modelMapper;
    private final MailSender mailSender;

    @Autowired
    public ResponseUtils(PostVersionRepository postVersionRepository,
                         CommentVersionRepository commentVersionRepository,
                         CommentRepository commentRepository,
                         LikePostRepository likePostRepository,
                         LikeCommentRepository likeCommentRepository,
                         ModelMapper modelMapper,
                         MailSender mailSender) {
        this.postVersionRepository = postVersionRepository;
        this.commentVersionRepository = commentVersionRepository;
        this.commentRepository = commentRepository;
        this.likePostRepository = likePostRepository;
        this.likeCommentRepository = likeCommentRepository;
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

    public List<?> pagingList(List<?> list, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), list.size());
        return list.subList(start, end);
    }

    public FriendshipDto getFriendshipInfo(Friendship friendship) {
        return friendship!=null?modelMapper.map(friendship, FriendshipDto.class):null;
    }

    public FriendRequestDto getFriendRequestInfo(FriendRequest friendRequest) {
        return friendRequest!=null?modelMapper.map(friendRequest, FriendRequestDto.class):null;
    }

    public UserDto getUserInfo(User user) {
        return user!=null?modelMapper.map(user, UserDto.class):null;
    }

    public ImageDto getImageInfo(Image image) {
        return new ImageDto(image.getPath(), this.getUserInfo(image.getUser()), image.getCreatedTime(), image.getImageType());
    }

    public ReportReqDto getReportRequestInfo(ReportRequest reportRequest){
        return new ReportReqDto(reportRequest.getReportRequestId(),
                this.getPostInfo(reportRequest.getPost()),
                this.getUserInfo(reportRequest.getCreatedUser()),
                reportRequest.getCreatedTime(),
                reportRequest.getReason(),
                this.getUserInfo(reportRequest.getAdmin()),
                reportRequest.getProcessedTime(),
                reportRequest.getRequestStatus(),
                reportRequest.getAction());
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
        PostDto postDto = new PostDto();
        postDto.setPostId(post.getPostId());
        postDto.setContent(lastVersion.getContent());
        postDto.setCreatedUser(this.getUserInfo(post.getCreatedUser()));
        postDto.setCreatedDate(post.getCreatedTime());
        postDto.setAudience(post.getAudience().getAudienceType());
        postDto.setCountLike(listLikes(post).size());
        postDto.setCountComment(countComments(post));
        return postDto;
    }

    public CommentDto getParrentComment(Comment comment) {
        CommentVersion lastVersion = getLastCommentVersion(comment);
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId(comment.getCommentId());
        commentDto.setCreatedUser(this.getUserInfo(comment.getCreatedUser()));
        commentDto.setContent(lastVersion.getContent());
        commentDto.setCreatedTime(lastVersion.getModifiedTime());
        commentDto.setCountLikes(listLikes(comment).size());
        commentDto.setCountComments(countComments(comment));
        if (comment.getParentComment() != null) {
            commentDto.setParrentComment(getParrentComment(comment.getParentComment()));
        } else {
            commentDto.setParrentComment(null);
        }
        return commentDto;
    }

    public CommentDto getChildComment(Comment comment) {
        CommentVersion lastVersion = getLastCommentVersion(comment);
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId(comment.getCommentId());
        commentDto.setCreatedUser(this.getUserInfo(comment.getCreatedUser()));
        commentDto.setContent(lastVersion.getContent());
        commentDto.setCreatedTime(lastVersion.getModifiedTime());
        commentDto.setCountLikes(listLikes(comment).size());
        commentDto.setCountComments(countComments(comment));
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

    public List<LikePost> listLikes(Post post) {
        return likePostRepository.findByPost(post);
    }

    public List<LikeComment> listLikes(Comment comment) {
        return likeCommentRepository.findByComment(comment);
    }

    public int countComments(Post post) {
        likePostRepository.findByPost(post);
        return 0;
    }

    public int countComments(Comment comment) {
        return 0;
    }
}
