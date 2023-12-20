package com.example.facebookbackend.utils;

import com.example.facebookbackend.dtos.response.CommentDto;
import com.example.facebookbackend.dtos.response.PostDto;
import com.example.facebookbackend.dtos.response.UserDto;
import com.example.facebookbackend.entities.*;
import com.example.facebookbackend.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    @Autowired
    public ResponseUtils(PostVersionRepository postVersionRepository,
                         CommentVersionRepository commentVersionRepository,
                         CommentRepository commentRepository,
                         LikePostRepository likePostRepository,
                         LikeCommentRepository likeCommentRepository,
                         ModelMapper modelMapper) {
        this.postVersionRepository = postVersionRepository;
        this.commentVersionRepository = commentVersionRepository;
        this.commentRepository = commentRepository;
        this.likePostRepository = likePostRepository;
        this.likeCommentRepository = likeCommentRepository;
        this.modelMapper = modelMapper;
    }

    public List<?> pagingList(List<?> list, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), list.size());
        return list.subList(start, end);
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
        postDto.setCreatedUser(modelMapper.map(post.getCreatedUser(), UserDto.class));
        postDto.setCreatedDate(post.getCreatedTime());
        postDto.setAudience(post.getAudience().getAudienceType());
        postDto.setCountLike(listLikes(post).size());
        postDto.setCountComment(countComments(post));
        return postDto;
    }

//    public CommentDto getParrentComment(Comment comment) {
//        CommentVersion lastVersion = getLastCommentVersion(comment);
//        CommentDto commentDto = new CommentDto();
//        commentDto.setCommentId(comment.getCommentId());
//        commentDto.setCreatedUser(modelMapper.map(comment.getCreatedUser(), UserDto.class));
//        commentDto.setContent(lastVersion.getContent());
//        commentDto.setCreatedTime(lastVersion.getModifiedTime());
//        commentDto.setCountLikes(listLikes(comment).size());
//        commentDto.setCountComments(countComments(comment));
//        if (comment.getParentComment() != null) {
//            commentDto.setParrentComment(getParrentComment(comment.getParentComment()));
//        } else {
//            commentDto.setParrentComment(null);
//        }
//        return commentDto;
//    }

    public CommentDto getChildComment(Comment comment) {
        CommentVersion lastVersion = getLastCommentVersion(comment);
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId(comment.getCommentId());
        commentDto.setCreatedUser(modelMapper.map(comment.getCreatedUser(), UserDto.class));
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
        return 0;
    }

    public int countComments(Comment comment) {
        return 0;
    }
}
