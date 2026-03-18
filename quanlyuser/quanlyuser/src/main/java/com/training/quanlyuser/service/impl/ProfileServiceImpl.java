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
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setSchool(user.getSchool());
        res.setDob(user.getDob());
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
        user.setDob(request.getDob());

        // upload avatar
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarUrl = uploadFile(request.getAvatar());
            user.setAvatarUrl(avatarUrl);
        }

        userRepository.save(user);
    }

    // =========================
    // ✅ 3. CHANGE PASSWORD
    // =========================
    @Override
    public void changePassword(ChangePasswordRequest request) {

        User user = getCurrentUser();

        // check confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu không khớp");
        }

        // check password cũ
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        // update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // giả lập upload file
    private String uploadFile(MultipartFile file) {
        return "/uploads/" + file.getOriginalFilename();
    }
}