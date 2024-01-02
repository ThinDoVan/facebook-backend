package com.example.facebookbackend.securities.services;

import com.example.facebookbackend.entities.User;
import com.example.facebookbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng "+email);
        } else {
            if (!user.get().isEnable()) {
                throw new RuntimeException("Tài khoản này đang bị vô hiệu hóa");
            }
            if (user.get().getLockUntil()!=null&&LocalDateTime.now().isBefore(user.get().getLockUntil())){
                LocalDateTime now = LocalDateTime.now();
                long remainingMiliSeconds = ChronoUnit.SECONDS.between(now, user.get().getLockUntil())*1000;

                throw new RuntimeException("Tài khoản bị tạm khóa. Vui lòng truy cập sau "
                + TimeUnit.MILLISECONDS.toHours(remainingMiliSeconds)+" giờ "
                + TimeUnit.MILLISECONDS.toMinutes(remainingMiliSeconds)%60+" phút "
                + TimeUnit.MILLISECONDS.toSeconds(remainingMiliSeconds)%60+" giây");
            }

            return UserDetailsImpl.build(user.get());
        }
    }
}
