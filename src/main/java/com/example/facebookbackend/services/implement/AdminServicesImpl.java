package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.ReportReqDto;
import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.ReportRequest;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.ERequestStatus;
import com.example.facebookbackend.enums.Email;
import com.example.facebookbackend.repositories.PostRepository;
import com.example.facebookbackend.repositories.ReportRequestRepository;
import com.example.facebookbackend.repositories.UserRepository;
import com.example.facebookbackend.services.AdminServices;
import com.example.facebookbackend.services.PostServices;
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
    public ResponseEntity<?> getListPostReport(Integer postId, Integer page, Integer size) {
        List<ReportRequest> reportRequestList;
        if (postId == null) {
            reportRequestList = reportRequestRepository.findAll();
        } else {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy bài viết có Id " + postId));
            } else {
                reportRequestList = reportRequestRepository.findByPost(post.get());
            }
        }
        List<ReportReqDto> reportReqDtoList = new ArrayList<>();
        for (ReportRequest reportRequest : reportRequestList) {
            reportReqDtoList.add(responseUtils.getReportRequestInfo(reportRequest));
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseUtils.pagingList(reportReqDtoList, page, size));
    }

    @Override
    public ResponseEntity<?> getReportRequest(Integer reportId) {
        Optional<ReportRequest> request = reportRequestRepository.findById(reportId);
        if (request.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy Report Request có Id " + reportId));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(responseUtils.getReportRequestInfo(request.get()));
        }
    }

    @Override
    public ResponseEntity<?> handleReport(User admin, Integer reportId, Boolean isApproved) {
        if (isApproved == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Thiếu nội dung xử lý"));
        } else {
            Optional<ReportRequest> request = reportRequestRepository.findById(reportId);
            if (request.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy Report Request có Id " + reportId));
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
                        System.out.println("\n\nĐỔI MẬT KHẨU:\n"+content);
                        userRepository.save(user);
                        reportRequestRepository.save(request.get());
                        return postServices.deletePost(admin, request.get().getPost().getPostId());
                    } else {
                        request.get().setRequestStatus(ERequestStatus.REJECTED);
                        request.get().setAction(null);
                        reportRequestRepository.save(request.get());
                        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Bạn đã từ chối Report Request này"));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Report Request này đã được xử lý"));
                }
            }
        }
    }
}
