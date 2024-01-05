package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.ReportRequestDto;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.ReportReqDto;
import com.example.facebookbackend.entities.*;
import com.example.facebookbackend.enums.ERequestStatus;
import com.example.facebookbackend.enums.Email;
import com.example.facebookbackend.exceptions.DataNotFoundException;
import com.example.facebookbackend.exceptions.InvalidDataException;
import com.example.facebookbackend.exceptions.NotAllowedException;
import com.example.facebookbackend.repositories.*;
import com.example.facebookbackend.services.PostServices;
import com.example.facebookbackend.services.ReportServices;
import com.example.facebookbackend.utils.AccessControlUtils;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReportServicesImpl implements ReportServices {
    @Autowired
    ResponseUtils responseUtils;
    @Autowired
    AccessControlUtils accessControlUtils;

    @Autowired
    ReportRequestRepository reportRequestRepository;
    @Autowired
    ReportPostRepository reportPostRepository;
    @Autowired
    ReportUserRepository reportUserRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostServices postServices;

    @Override
    public MessageResponse reportPost(User currentUser, ReportRequestDto reportRequestDto) {
        Optional<Post> post = postRepository.findById(reportRequestDto.getId());
        if (post.isEmpty() || post.get().isDeleted()) {
            throw new DataNotFoundException("Không tìm thấy bài viết có id: " + reportRequestDto.getId() + " hoặc bài viết đã bị xóa");
        } else {
            if (accessControlUtils.checkReadPermission(currentUser, post.get())) {
                if (reportPostRepository.findByPostAndCreatedUser(post.get(), currentUser).isPresent()) {
                    return new MessageResponse("Bạn đã gửi báo cáo về bài viết này trước đây");
                } else {
//                    ReportRequest request = new ReportRequest(post.get(),
//                            currentUser,
//                            LocalDateTime.now(),
//                            reportRequestDto.getReason());
//                    reportRequestRepository.save(request);
                    ReportPost reportPost = new ReportPost(post.get(),
                            currentUser,
                            LocalDateTime.now(),
                            reportRequestDto.getReason());
                    reportPostRepository.save(reportPost);
                    post.get().setCountReported(post.get().getCountReported() + 1);
                    postRepository.save(post.get());
                    return new MessageResponse("Cảm ơn bạn đã gửi báo cáo bài viết. Chúng tôi sẽ xem xét và xử lý trường hợp này");
                }
            } else {
                throw new NotAllowedException("Bạn không có quyền xem bài viết");
            }
        }
    }

    @Override
    public MessageResponse reportUser(User currentUser, ReportRequestDto reportRequestDto) {
        Optional<User> user = userRepository.findById(reportRequestDto.getId());
        if (user.isEmpty() || !user.get().isEnable()) {
            throw new DataNotFoundException("Không tìm thấy người dùng có id: " + reportRequestDto.getId());
        } else {
            if (reportUserRepository.findByUserAndCreatedUser(user.get(), currentUser).isPresent()) {
                return new MessageResponse("Bạn đã gửi báo cáo về người dùng này trước đây");
            } else {
                ReportUser request = new ReportUser(user.get(),
                        currentUser,
                        LocalDateTime.now(),
                        reportRequestDto.getReason());
                reportUserRepository.save(request);

                return new MessageResponse("Cảm ơn bạn đã gửi báo cáo người dùng. Chúng tôi sẽ xem xét và xử lý trường hợp này");
            }
        }
    }

    @Override
    public Page<ReportReqDto> getListReportUserRequest(Integer userId, String requestStatus, Integer page, Integer size) {
        List<ReportUser> reportUserList = reportUserRepository.findAll().stream()
                .filter(reportRequest -> userId == null || (reportRequest.getUser() != null && Objects.equals(reportRequest.getUser().getUserId(), userId)))
                .filter(reportRequest -> {
                    if (requestStatus != null && !requestStatus.isEmpty()) {
                        switch (requestStatus.toLowerCase()) {
                            case "pending" -> {
                                return reportRequest.getRequestStatus().equals(ERequestStatus.PENDING);
                            }
                            case "approved" -> {
                                return reportRequest.getRequestStatus().equals(ERequestStatus.APPROVED);
                            }
                            case "rejected" -> {
                                return reportRequest.getRequestStatus().equals(ERequestStatus.REJECTED);
                            }
                            default ->
                                    throw new InvalidDataException("Không tồn tại trạng thái " + requestStatus + ". Các trạng thái khả dụng: pending, approved, rejected");
                        }
                    } else {
                        return true;
                    }
                })
                .toList();
        List<ReportReqDto> reportReqDtoList = new ArrayList<>();
        for (ReportUser reportUser : reportUserList) {
            reportReqDtoList.add(responseUtils.getReportUserInfo(reportUser));
        }
        return responseUtils.pagingList(reportReqDtoList, page, size);
    }

    @Override
    public Page<ReportReqDto> getListReportPostRequest(Integer postId, String requestStatus, Integer page, Integer size) {
        List<ReportPost> reportPostList = reportPostRepository.findAll().stream()
                .filter(reportRequest -> postId == null || (reportRequest.getPost() != null && Objects.equals(reportRequest.getPost().getPostId(), postId)))
                .filter(reportRequest -> {
                    if (requestStatus != null && !requestStatus.isEmpty()) {
                        switch (requestStatus.toLowerCase()) {
                            case "pending" -> {
                                return reportRequest.getRequestStatus().equals(ERequestStatus.PENDING);
                            }
                            case "approved" -> {
                                return reportRequest.getRequestStatus().equals(ERequestStatus.APPROVED);
                            }
                            case "rejected" -> {
                                return reportRequest.getRequestStatus().equals(ERequestStatus.REJECTED);
                            }
                            default ->
                                    throw new InvalidDataException("Không tồn tại trạng thái " + requestStatus + ". Các trạng thái khả dụng: pending, approved, rejected");
                        }
                    } else {
                        return true;
                    }
                })
                .toList();
        List<ReportReqDto> reportReqDtoList = new ArrayList<>();
        for (ReportPost reportPost : reportPostList) {
            reportReqDtoList.add(responseUtils.getReportPostInfo(reportPost));
        }
        return responseUtils.pagingList(reportReqDtoList, page, size);
    }

//    @Override
//    public Page<ReportReqDto> getListReport(Integer postId, Integer userId, String requestStatus, Integer page, Integer size) {
//        List<ReportRequest> reportRequestList = reportRequestRepository.findAll().stream()
//                .filter(reportRequest -> postId == null || (reportRequest.getPost() != null && Objects.equals(reportRequest.getPost().getPostId(), postId)))
//                .filter(reportRequest -> userId == null || (reportRequest.getUser() != null && Objects.equals(reportRequest.getUser().getUserId(), userId)))
//                .filter(reportRequest -> {
//                    if (requestStatus != null && !requestStatus.isEmpty()) {
//                        switch (requestStatus.toLowerCase()) {
//                            case "pending" -> {
//                                return reportRequest.getRequestStatus().equals(ERequestStatus.PENDING);
//                            }
//                            case "approved" -> {
//                                return reportRequest.getRequestStatus().equals(ERequestStatus.APPROVED);
//                            }
//                            case "rejected" -> {
//                                return reportRequest.getRequestStatus().equals(ERequestStatus.REJECTED);
//                            }
//                            default ->
//                                    throw new InvalidDataException("Không tồn tại trạng thái " + requestStatus + ". Các trạng thái khả dụng: pending, approved, rejected");
//                        }
//                    } else {
//                        return true;
//                    }
//                })
//                .toList();
//
//        List<ReportReqDto> reportReqDtoList = new ArrayList<>();
//        for (ReportRequest reportRequest : reportRequestList) {
//            reportReqDtoList.add(responseUtils.getReportRequestInfo(reportRequest));
//        }
//        return responseUtils.pagingList(reportReqDtoList, page, size);
//    }

//    @Override
//    public ReportReqDto getReportRequest(Integer reportId) {
//        Optional<ReportRequest> request = reportRequestRepository.findById(reportId);
//        if (request.isEmpty()) {
//            throw new DataNotFoundException("Không tìm thấy Report Request có Id " + reportId);
//        } else {
//            return responseUtils.getReportRequestInfo(request.get());
//        }
//    }

    @Override
    public ReportReqDto getReportUserRequest(Integer reportId) {
        Optional<ReportUser> reportUser = reportUserRepository.findById(reportId);
        if (reportUser.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy Report User có Id " + reportId);
        } else {
            return responseUtils.getReportUserInfo(reportUser.get());

        }
    }

    @Override
    public ReportReqDto getReportPostRequest(Integer reportId) {
        Optional<ReportPost> reportPost = reportPostRepository.findById(reportId);
        if (reportPost.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy Report Post có Id " + reportId);
        } else {
            return responseUtils.getReportPostInfo(reportPost.get());
        }
    }

    @Override
    public MessageResponse handleReportUser(User admin, Integer reportId, Boolean isApproved) {
        Optional<ReportUser> reportUser = reportUserRepository.findById(reportId);
        if (reportUser.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy Report Request có Id " + reportId);
        } else {
            if (reportUser.get().getRequestStatus() == ERequestStatus.PENDING) {

                reportUser.get().setAdmin(admin);
                reportUser.get().setProcessedTime(LocalDateTime.now());
                if (isApproved) {
                    User user = reportUser.get().getUser();
                    reportUser.get().setRequestStatus(ERequestStatus.APPROVED);


                    user.setLockUntil(LocalDateTime.now().plusHours(48));
                    reportUser.get().setAction("Khóa tài khoản trong 48h");
                    String content = Email.LOCK_ACCOUNT.getContent()
                            .replace("${username}", user.getFullName())
                            .replace("${reason}", reportUser.get().getReason())
                            .replace("${action}", reportUser.get().getAction());
                    responseUtils.sendEmail(user, Email.LOCK_ACCOUNT.getSubject(), content);
                    userRepository.save(user);
                    return new MessageResponse("Tài khoản người dùng này bị khóa trong 48h");
                } else {
                    reportUser.get().setRequestStatus(ERequestStatus.REJECTED);
                    reportUser.get().setAction(null);
                    reportUserRepository.save(reportUser.get());
                    return new MessageResponse("Bạn đã từ chối Report Request này");
                }
            } else {
                return new MessageResponse("Report Request này đã được xử lý");
            }
        }
    }

    @Override
    public MessageResponse handleReportPost() {
        User admin = userRepository.findById(1).orElseThrow(() -> new DataNotFoundException("Không tìm thấy Admin"));
        List<Post> postList = postRepository.findAllByCountReportedGreaterThanEqual(3).stream()
                .filter(post -> !post.isDeleted())
                .toList();
        for (Post post : postList) {
            List<ReportPost> reportPostList = reportPostRepository.findByPost(post).stream()
                    .filter(reportPost -> reportPost.getRequestStatus().equals(ERequestStatus.PENDING))
                    .toList();
            for (ReportPost reportPost : reportPostList) {
                reportPost.setAction("Xóa bài viết");
                reportPost.setRequestStatus(ERequestStatus.APPROVED);
                reportPost.setProcessedTime(LocalDateTime.now());
                reportPost.setAdmin(admin);
                reportPostRepository.save(reportPost);
            }
            postServices.deletePost(admin, post.getPostId());
        }
        return new MessageResponse("Xử lý thành công");
    }

//    @Override
//    public MessageResponse handleReport(User admin, Integer reportId, Boolean isApproved) {
//        if (isApproved == null) {
//            throw new InvalidDataException("Thiếu nội dung xử lý.");
//        } else {
//            Optional<ReportRequest> request = reportRequestRepository.findById(reportId);
//            if (request.isEmpty()) {
//                throw new DataNotFoundException("Không tìm thấy Report Request có Id " + reportId);
//            } else {
//                if (request.get().getRequestStatus() == ERequestStatus.PENDING) {
//
//                    request.get().setAdmin(admin);
//                    request.get().setProcessedTime(LocalDateTime.now());
//                    if (isApproved) {
//                        Post post = request.get().getPost();
//                        User user = request.get().getUser();
//                        request.get().setRequestStatus(ERequestStatus.APPROVED);
//
//                        if (post != null) {
//                            User postAuthor = post.getCreatedUser();
//                            postAuthor.setViolationCount(postAuthor.getViolationCount() + 1);
//
//                            if (postAuthor.getViolationCount() >= 3) {
//                                postAuthor.setLockUntil(LocalDateTime.now().plusHours(24));
//                                request.get().setAction("Xóa bài viết và Khóa tài khoản trong vòng 24h");
//                                String content = Email.LOCK_ACCOUNT.getContent()
//                                        .replace("${username}", postAuthor.getFullName())
//                                        .replace("${reason}","Tài khoản đăng tải bài viết vi phạm tiêu chuẩn cộng đồng 3 lần")
//                                        .replace("${action}",request.get().getAction());
//                                responseUtils.sendEmail(postAuthor, Email.LOCK_ACCOUNT.getSubject(), content);
//                            }else {
//                                request.get().setAction("Xóa bài viết");
//                                String content = Email.REMOVE_POST.getContent()
//                                        .replace("${username}", postAuthor.getFullName())
//                                        .replace("${violationCount}", String.valueOf(postAuthor.getViolationCount()))
//                                        .replace("${action}",request.get().getAction());
//                                responseUtils.sendEmail(postAuthor, Email.REMOVE_POST.getSubject(), content);
//                            }
//
//
//
//                            userRepository.save(postAuthor);
//                            reportRequestRepository.save(request.get());
//
//                            return postServices.deletePost(admin, request.get().getPost().getPostId());
//                        } else {
//                            user.setLockUntil(LocalDateTime.now().plusHours(48));
//                            request.get().setAction("Khóa tài khoản trong 48h");
//                            String content = Email.LOCK_ACCOUNT.getContent()
//                                    .replace("${username}", user.getFullName())
//                                    .replace("${reason}",request.get().getReason())
//                                    .replace("${action}",request.get().getAction());
//                            responseUtils.sendEmail(user, Email.LOCK_ACCOUNT.getSubject(), content);
//                            userRepository.save(user);
//                            return new MessageResponse("Tài khoản người dùng này bị khóa trong 48h");
//                        }
//                    } else {
//                        request.get().setRequestStatus(ERequestStatus.REJECTED);
//                        request.get().setAction(null);
//                        reportRequestRepository.save(request.get());
//                        return new MessageResponse("Bạn đã từ chối Report Request này");
//                    }
//                } else {
//                    return new MessageResponse("Report Request này đã được xử lý");
//                }
//            }
//        }
//    }
}