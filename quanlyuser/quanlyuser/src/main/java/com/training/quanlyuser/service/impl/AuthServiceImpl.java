package com.training.quanlyuser.service.impl;

import com.training.quanlyuser.dto.request.LoginRequest;
import com.training.quanlyuser.dto.response.LoginResponse;
import com.training.quanlyuser.entity.User;
import com.training.quanlyuser.exception.BadRequestException;
import com.training.quanlyuser.exception.NotFoundException;
import com.training.quanlyuser.repository.UserRepository;
import com.training.quanlyuser.security.JwtUtil;
import com.training.quanlyuser.service.AuthService;
import com.training.quanlyuser.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid password");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BadRequestException("Account is not active");
        }

        // ✅ lấy role đúng (1 role)
        String role = user.getRole() != null
                ? user.getRole().getName()
                : "USER";

        String token = jwtUtil.generateToken(user.getUsername(), role);

        LoginResponse response = new LoginResponse();
        response.setUsername(user.getUsername());
        response.setToken(token);
        response.setRole(role);

        return response;
    }

    private final EmailService emailService;
    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        String link = "http://127.0.0.1:5500/reset-password.html?token=" + token;

        emailService.sendEmail(email, "Reset Password", link);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token đã hết hạn");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiry(null);

        userRepository.save(user);
    }
}