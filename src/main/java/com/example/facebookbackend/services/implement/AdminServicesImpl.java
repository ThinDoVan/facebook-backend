package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.ReportReqDto;
import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.ReportRequest;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.ERequestStatus;
import com.example.facebookbackend.enums.Email;
import com.example.facebookbackend.exceptions.DataNotFoundException;
import com.example.facebookbackend.exceptions.InvalidDataException;
import com.example.facebookbackend.repositories.PostRepository;
import com.example.facebookbackend.repositories.ReportRequestRepository;
import com.example.facebookbackend.repositories.UserRepository;
import com.example.facebookbackend.services.AdminServices;
import com.example.facebookbackend.services.PostServices;
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
public class AdminServicesImpl implements AdminServices {
    @Autowired
    ResponseUtils responseUtils;
    @Autowired
    AccessControlUtils accessControlUtils;

    @Autowired
    ReportRequestRepository reportRequestRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    PostServices postServices;

    @Override
    public Page<ReportReqDto> getListPostReport(Integer postId, Integer page, Integer size) {
        List<ReportRequest> reportRequestList;
        if (postId == null) {
            reportRequestList = reportRequestRepository.findAll();
        } else {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isEmpty()) {
                throw new DataNotFoundException("Không tìm thấy bài viết có Id " + postId);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có Id " + postId));
            } else {
                reportRequestList = reportRequestRepository.findByPost(post.get());
            }
        }
        List<ReportReqDto> reportReqDtoList = new ArrayList<>();
        for (ReportRequest reportRequest : reportRequestList) {
            reportReqDtoList.add(responseUtils.getReportRequestInfo(reportRequest));
        }
        return responseUtils.pagingList(reportReqDtoList, page, size);
//        return ResponseEntity.status(HttpStatus.OK).body(responseUtils.pagingList(reportReqDtoList, page, size));
    }

    @Override
    public ReportReqDto getReportRequest(Integer reportId) {
        Optional<ReportRequest> request = reportRequestRepository.findById(reportId);
        if (request.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy Report Request có Id " + reportId);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy Report Request có Id " + reportId));
        } else {
            return responseUtils.getReportRequestInfo(request.get());
//            return ResponseEntity.status(HttpStatus.OK).body(responseUtils.getReportRequestInfo(request.get()));
        }
    }

    @Override
    public MessageResponse handleReport(User admin, Integer reportId, Boolean isApproved) {
        if (isApproved == null) {
            throw new InvalidDataException("Thiếu nội dung xử lý.");
        } else {
            Optional<ReportRequest> request = reportRequestRepository.findById(reportId);
            if (request.isEmpty()) {
                throw new DataNotFoundException("Không tìm thấy Report Request có Id "+ reportId);
            } else {
                if (request.get().getRequestStatus() == ERequestStatus.PENDING) {
                    Post post = request.get().getPost();
                    User user = post.getCreatedUser();
                    request.get().setAdmin(admin);
                    request.get().setProcessedTime(LocalDateTime.now());

                    if (isApproved) {
                        request.get().setRequestStatus(ERequestStatus.APPROVED);
                        request.get().setAction("Xóa bài viết");
                        user.setLockUntil(LocalDateTime.now().plusHours(24));
//                        Gửi mail thông báo
                        String content = Email.REMOVE_POST.getContent()
                                .replace("${username}", request.get().getPost().getCreatedUser().getFullName());
//                        responseUtils.sendEmail(request.get().getPost().getCreatedUser(), Email.REMOVE_POST.getSubject(), content);
                        userRepository.save(user);
                        reportRequestRepository.save(request.get());
//                        return postServices.deletePost(admin, request.get().getPost().getPostId());
                        return postServices.deletePost(admin, request.get().getPost().getPostId());

                    } else {
                        request.get().setRequestStatus(ERequestStatus.REJECTED);
                        request.get().setAction(null);
                        reportRequestRepository.save(request.get());
                        return new MessageResponse("Bạn đã từ chối Report Request này");
//                        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Bạn đã từ chối Report Request này"));
                    }
                } else {
                    return new MessageResponse("Report Request này đã được xử lý");
//                    return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Report Request này đã được xử lý"));
                }
            }
        }
    }
}
