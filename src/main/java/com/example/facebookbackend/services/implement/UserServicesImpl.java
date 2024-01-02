package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.LoginRequest;
import com.example.facebookbackend.dtos.request.RegisterRequest;
import com.example.facebookbackend.dtos.request.ResetPasswordRequest;
import com.example.facebookbackend.dtos.response.JwtResponse;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.UserDto;
import com.example.facebookbackend.entities.Role;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.entities.VerificationCode;
import com.example.facebookbackend.enums.ERole;
import com.example.facebookbackend.enums.Email;
import com.example.facebookbackend.exceptions.DataNotFoundException;
import com.example.facebookbackend.exceptions.InvalidDataException;
import com.example.facebookbackend.repositories.*;
import com.example.facebookbackend.securities.jwt.JwtUtils;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.UserServices;
import com.example.facebookbackend.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
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
    public MessageResponse registerAccount(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new InvalidDataException("Email này đã được sử dụng");
        }
        if (registerRequest.getDateOfBirth() != null && registerRequest.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new InvalidDataException("Ngày sinh không hợp lệ");
        }
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setDateOfBirth(registerRequest.getDateOfBirth());
        user.setGender(registerRequest.getGender());
        Set<Role> roleSet = new HashSet<>();
        if (userRepository.findAll().isEmpty()) {
            roleSet.add(roleRepository.findByRole(ERole.ROLE_ADMIN).orElseThrow(() -> new DataNotFoundException("Không tìm thấy Role")));
        }
        roleSet.add(roleRepository.findByRole(ERole.ROLE_USER).orElseThrow(() -> new DataNotFoundException("Không tìm thấy Role")));
        user.setRoleSet(roleSet);
        user.setEnable(false);
        user.setCreatedTime(LocalDateTime.now());
        userRepository.save(user);
        createVerificationCode(user, Email.REGISTER);
        return new MessageResponse("Tạo tài khoản thành công");
    }

    @Override
    public MessageResponse activeAccount(String verificationCodeStr) {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findByVerificationCode(verificationCodeStr);
        if (verificationCode.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy Verification Code");
        } else {
            if (verificationCode.get().getExpiredTime().isBefore(LocalDateTime.now())) {
                return new MessageResponse("Verification Code quá hạn");
            } else {
                User user = verificationCode.get().getUser();
                user.setEnable(true);
                userRepository.save(user);
                return new MessageResponse("Tài khoản của bạn đã được active");
            }
        }
    }

    @Override
    public JwtResponse loginAccount(LoginRequest loginRequest) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Set<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            return new JwtResponse(jwt, userDetails.getUsername(), roles);

    }


    @Override
    public UserDto getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() || !user.get().isEnable()) {
            throw new DataNotFoundException("Không tìm thấy người dùng có email: " + email);
        } else {
            return responseUtils.getUserInfo(user.get());
        }
    }

    @Override
    public UserDto getUserById(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty() || !user.get().isEnable()) {
            throw new DataNotFoundException("Không tìm thấy người dùng có Id: " + userId);
        } else {
            return responseUtils.getUserInfo(user.get());
        }
    }

    @Override
    public MessageResponse updateUserInfo(User currentUser, UserDto userInfo) {
        if (userInfo.getFullname() != null && !userInfo.getFullname().isEmpty() && !userInfo.getFullname().isBlank()) {
            currentUser.setFullName(userInfo.getFullname());
        }
        if (userInfo.getDateOfBirth() != null) {
            currentUser.setDateOfBirth(userInfo.getDateOfBirth());
        }
        if (userInfo.getGender() != null && !userInfo.getGender().isEmpty() && !userInfo.getGender().isBlank()) {
            currentUser.setGender(userInfo.getGender());
        }
        if (userInfo.getAddress() != null && !userInfo.getAddress().isEmpty() && !userInfo.getAddress().isBlank()) {
            currentUser.setAddress(userInfo.getAddress());
        }
        if (userInfo.getCity() != null && !userInfo.getCity().isEmpty() && !userInfo.getCity().isBlank()) {
            currentUser.setCity(userInfo.getCity());
        }
        if (userInfo.getSchool() != null && !userInfo.getSchool().isEmpty() && !userInfo.getSchool().isBlank()) {
            currentUser.setSchool(userInfo.getSchool());
        }
        if (userInfo.getCompany() != null && !userInfo.getCompany().isEmpty() && !userInfo.getCompany().isBlank()) {
            currentUser.setCompany(userInfo.getCompany());
        }
        userRepository.save(currentUser);
        return new MessageResponse("Cập nhật thông tin cá nhân thành công");
    }

    @Override
    public MessageResponse changePassword(User currentUser, String currentPassword) {
        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            return new MessageResponse("Đổi mật khẩu thất bại");
        } else {
            createVerificationCode(currentUser, Email.CHANGE_PASSWORD);
            return new MessageResponse("Hệ thống đã gửi mã xác nhận đến email của bạn. Vui lòng kiểm tra");
        }
    }

    @Override
    public MessageResponse forgotPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() || !user.get().isEnable()) {
            throw new DataNotFoundException("Không tìm thấy người dùng có email " + email);
        } else {
            createVerificationCode(user.get(), Email.CHANGE_PASSWORD);
            return new MessageResponse("Hệ thống đã gửi mã xác nhận đến email của bạn. Vui lòng kiểm tra");
        }
    }

    @Override
    public MessageResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findByVerificationCode(resetPasswordRequest.getVerificationCode());
        if (verificationCode.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy Verification Code");
        } else {
            if (verificationCode.get().getExpiredTime().isBefore(LocalDateTime.now())) {
                return new MessageResponse("Verification Code quá hạn");
            } else {
                User user = verificationCode.get().getUser();
                user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
                userRepository.save(user);
                verificationCodeRepository.delete(verificationCode.get());
                return new MessageResponse("Đổi mật khẩu thành công");
            }
        }
    }

    @Override
    public MessageResponse disableAccount(User currentUser) {
        return null;
    }

    private String generateOTP() {
        String allowedChars = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(allowedChars.length());
            otp.append(allowedChars.charAt(index));
        }
        return otp.toString();
    }

    private void createVerificationCode(User user, Email emailType) {
        Optional<VerificationCode> currentVerificationCode = verificationCodeRepository.findByUser(user);
        currentVerificationCode.ifPresent(verificationCode -> verificationCodeRepository.delete(verificationCode));
        VerificationCode verificationCode = new VerificationCode(generateOTP(), user);
        String content = emailType.getContent()
                .replace("${code}", verificationCode.getVerificationCode())
                .replace("${username}", user.getFullName())
                .replace("${time}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
//            responseUtils.sendEmail(user, emailType.getSubject(), content);
        verificationCode.setExpiredTime(LocalDateTime.now().plusMinutes(5));
        verificationCodeRepository.save(verificationCode);
    }
}