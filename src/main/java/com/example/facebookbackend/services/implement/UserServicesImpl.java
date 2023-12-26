package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.ChangePasswordRequest;
import com.example.facebookbackend.dtos.request.LoginRequest;
import com.example.facebookbackend.dtos.request.RegisterRequest;
import com.example.facebookbackend.dtos.request.ResetPasswordRequest;
import com.example.facebookbackend.dtos.response.JwtResponse;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.entities.VerificationCode;
import com.example.facebookbackend.entities.Role;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.ERole;
import com.example.facebookbackend.enums.Email;
import com.example.facebookbackend.repositories.*;
import com.example.facebookbackend.securities.jwt.JwtUtils;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.UserServices;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServicesImpl implements UserServices {
    @Autowired
    ResponseUtils responseUtils;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    FriendRequestRepository friendRequestRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    VerificationCodeRepository verificationCodeRepository;


    @Override
    public ResponseEntity<MessageResponse> registerAccount(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Email này đã được sử dụng"));
        }
        if (registerRequest.getDateOfBirth() != null && registerRequest.getDateOfBirth().isAfter(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Ngày sinh không hợp lệ"));
        }
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setDateOfBirth(registerRequest.getDateOfBirth());
        user.setGender(registerRequest.getGender());
        Set<Role> roleSet = new HashSet<>();
        if (userRepository.findAll().isEmpty()) {
            roleSet.add(roleRepository.findByRole(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role")));
        }
        roleSet.add(roleRepository.findByRole(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role")));
        user.setRoleSet(roleSet);

        user.setEnable(true);
        user.setCreatedTime(LocalDateTime.now());
        userRepository.save(user);

//        String content = Email.REGISTER.getContent()
//                .replace("${username}", user.getFullName())
//                .replace("${time}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
//        responseUtils.sendEmail(user, Email.REGISTER.getSubject(), content);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Tạo tài khoản thành công"));
    }

    @Override
    public ResponseEntity<?> loginAccount(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Set<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            return ResponseEntity.status(HttpStatus.OK).body(new JwtResponse(jwt, userDetails.getUsername(), roles));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Đăng nhập thất bại. " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<MessageResponse> changePassword(User currentUser, ChangePasswordRequest changePasswordRequest) {
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), currentUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Đổi mật khẩu thất bại"));
        } else {
            currentUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(currentUser);
//            String content = Email.CHANGE_PASSWORD.getContent()
//                    .replace("${username}", currentUser.getFullName())
//                    .replace("${time}",LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
//            responseUtils.sendEmail(currentUser, Email.CHANGE_PASSWORD.getSubject(), content);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Đổi mật khẩu thành công"));
        }
    }


    @Override
    public ResponseEntity<?> getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() || !user.get().isEnable()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Không tìm thấy người dùng có email: " + email));
        } else {

            return ResponseEntity.ok().body(responseUtils.getUserInfo(user.get()));
        }
    }

    @Override
    public ResponseEntity<?> getUserById(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty() || !user.get().isEnable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có Id: " + userId));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(responseUtils.getUserInfo(user.get()));
        }
    }

    @Override
    public ResponseEntity<MessageResponse> forgotPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()|| !user.get().isEnable()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy người dùng có email "+email));
        }else {
            Optional<VerificationCode> currentVerificationCode = verificationCodeRepository.findByUser(user.get());
            if (currentVerificationCode.isPresent()){
                verificationCodeRepository.delete(currentVerificationCode.get());
            }
            VerificationCode verificationCode = new VerificationCode(generateOTP(8), user.get());
            String content = Email.FORGOT_PASSWORD.getContent()
                    .replace("${code}", verificationCode.getVerificationCode())
                    .replace("${name}", user.get().getFullName())
                    .replace("${time}",LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            responseUtils.sendEmail(user.get(), Email.FORGOT_PASSWORD.getSubject(), content);
            verificationCode.setExpiredTime(LocalDateTime.now().plusMinutes(5));
            verificationCodeRepository.save(verificationCode);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Hệ thống đã gửi mã xác nhận đến email của bạn. Vui lòng kiểm tra"));
        }
    }

    @Override
    public ResponseEntity<MessageResponse> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findByVerificationCode(resetPasswordRequest.getVerificationCode());
        if(verificationCode.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Không tìm thấy Verification Code"));
        }else {
            if(verificationCode.get().getExpiredTime().isBefore(LocalDateTime.now())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Verification Code quá hạn"));
            }else {
                User user = verificationCode.get().getUser();
                user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
                userRepository.save(user);
                verificationCodeRepository.delete(verificationCode.get());
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Đổi mật khẩu thành công"));
            }
        }
    }
    public String generateOTP(int length) {
        String allowedChars = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allowedChars.length());
            otp.append(allowedChars.charAt(index));
        }

        return otp.toString();
    }
}