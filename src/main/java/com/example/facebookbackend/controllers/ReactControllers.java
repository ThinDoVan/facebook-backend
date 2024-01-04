package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.request.CommentRequest;
import com.example.facebookbackend.dtos.request.ReportRequestDto;
import com.example.facebookbackend.dtos.response.CommentDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.ReactServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/react")
public class ReactControllers {
    @Autowired
    private ReactServices reactServices;

    @PostMapping(path = "/LikePost")
    public ResponseEntity<MessageResponse> likePost(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam Integer postId) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(reactServices.likePost(currentUser, postId));
    }

    @PostMapping(path = "/CommentPost")
    public ResponseEntity<MessageResponse> commentPost(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody CommentRequest commentRequest) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(reactServices.commentPost(currentUser, commentRequest));
    }

    @PostMapping(path = "/LikeComment")
    public ResponseEntity<MessageResponse> likeComment(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestParam Integer commentId) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(reactServices.likeComment(currentUser, commentId));
    }

    @PostMapping(path = "/ReplyComment")
    public ResponseEntity<MessageResponse> replyComment(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestBody CommentRequest commentRequest) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(reactServices.replyComment(currentUser, commentRequest));
    }

    @PutMapping(path = "/UpdateComment")
    public ResponseEntity<MessageResponse> updateComment(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody CommentRequest commentRequest){
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(reactServices.updateComment(currentUser, commentRequest));
    }

    @DeleteMapping(path = "/DeleteComment")
    public ResponseEntity<MessageResponse> deleteComment(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestParam Integer commentId){
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(reactServices.deleteComment(currentUser, commentId));
    }

    @GetMapping(path = "/GetCommentList")
    public ResponseEntity<Page<CommentDto>> getPostComments(@AuthenticationPrincipal UserDetails userDetails,
                                                            @RequestParam Integer postId,
                                                            @RequestParam(required = false, defaultValue = "0") Integer page,
                                                            @RequestParam(required = false, defaultValue = "5") Integer size){
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        Page<CommentDto> result = reactServices.getPostComments(currentUser, postId, page, size);
        return ResponseEntity.ok(result);
    }


    @PostMapping(path = "/ReportPost")
    public ResponseEntity<MessageResponse> reportPost(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody ReportRequestDto reportRequestDto) {
        User currentUser = ((UserDetailsImpl) userDetails).getUser();
        return ResponseEntity.ok(reactServices.reportPost(currentUser, reportRequestDto));
    }
}
