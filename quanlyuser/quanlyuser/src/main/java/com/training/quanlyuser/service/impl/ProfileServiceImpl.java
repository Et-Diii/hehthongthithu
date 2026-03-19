package com.training.quanlyuser.service.impl;

import com.training.quanlyuser.dto.request.ChangePasswordRequest;
import com.training.quanlyuser.dto.request.UpdateProfileRequest;
import com.training.quanlyuser.dto.response.ProfileResponse;
import com.training.quanlyuser.entity.User;
import com.training.quanlyuser.repository.UserRepository;
import com.training.quanlyuser.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


//hà hồng dương -16/3/2026
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 🔹 Lấy user đang login
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println("🔥 Username từ SecurityContext: " + username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }

    // =========================
    // ✅ 1. GET PROFILE
    // =========================
    @Override
    public ProfileResponse getProfile() {
        User user = getCurrentUser();

        ProfileResponse res = new ProfileResponse();
        res.setUsername(user.getUsername());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setSchool(user.getSchool());
        res.setDateOfBirth(user.getDob());
        res.setAvatar(user.getAvatarUrl());

        return res;
    }

    // =========================
    // ✅ 2. UPDATE PROFILE
    // =========================
    @Override
    public void updateProfile(UpdateProfileRequest request) {

        User user = getCurrentUser();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setSchool(request.getSchool());
        user.setDob(request.getDateOfBirth());


        userRepository.save(user);
    }

    // =========================
    // ✅ 3. CHANGE PASSWORD
    // Thay đổi tham số truyền vào, thêm String username
    @Override
    public void changePassword(String username, ChangePasswordRequest request) {

        // Tìm user dựa trên username được truyền từ Controller xuống
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // ❌ check null
        if (request.getCurrentPassword() == null ||
                request.getNewPassword() == null ||
                request.getConfirmPassword() == null) {
            throw new RuntimeException("Không được để trống");
        }

        // ❌ confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu không khớp");
        }

        // ❌ password cũ sai
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        // ❌ không cho trùng mật khẩu cũ
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu mới phải khác mật khẩu cũ");
        }

        // ✅ update
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // giả lập upload file
    private String uploadFile(MultipartFile file) {
        return "/uploads/" + file.getOriginalFilename();
    }
}