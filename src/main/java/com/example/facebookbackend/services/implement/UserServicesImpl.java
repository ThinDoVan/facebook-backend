package com.example.facebookbackend.services.implement;

import com.example.facebookbackend.dtos.request.LoginRequest;
import com.example.facebookbackend.dtos.request.RegisterRequest;
import com.example.facebookbackend.dtos.response.JwtResponse;
import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.dtos.response.UserResponse;
import com.example.facebookbackend.entities.Role;
import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.enums.ERole;
import com.example.facebookbackend.repositories.FriendRequestRepository;
import com.example.facebookbackend.repositories.RoleRepository;
import com.example.facebookbackend.repositories.UserRepository;
import com.example.facebookbackend.securities.jwt.JwtUtils;
import com.example.facebookbackend.securities.services.UserDetailsImpl;
import com.example.facebookbackend.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServicesImpl implements UserServices {
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
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Tạo tài khoản thành công"));
    }

    @Override
    public ResponseEntity<?> loginAccount(LoginRequest loginRequest) {
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toSet());
        return ResponseEntity.status(HttpStatus.OK).body(new JwtResponse(jwt, userDetails.getUsername(), roles));
        }catch (AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Đăng nhập thất bại."));
        }
    }

    @Override
    public ResponseEntity<?> getUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() || !user.get().isEnable()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Không tồn tại người dùng có email: " + email));
        } else {
            return ResponseEntity.ok().body(new UserResponse(user.get().getUserId(),
                    user.get().getFullName(),
                    user.get().getDateOfBirth(),
                    user.get().getGender()));
        }
    }

}
