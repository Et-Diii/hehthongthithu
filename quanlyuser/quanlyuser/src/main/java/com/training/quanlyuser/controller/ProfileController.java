package com.training.quanlyuser.controller;

import com.training.quanlyuser.dto.request.ChangePasswordRequest;
import com.training.quanlyuser.dto.request.UpdateProfileRequest;
import com.training.quanlyuser.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // ✅ 1. Lấy thông tin profile (hiển thị bên trái)
    @GetMapping
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    // ✅ 2. Update thông tin cá nhân
    @PutMapping
    public ResponseEntity<?> updateProfile(@ModelAttribute UpdateProfileRequest request) {
        profileService.updateProfile(request);
        return ResponseEntity.ok("Cập nhật thành công");
    }

    // ✅ 3. Đổi mật khẩu (tab bảo mật)
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        profileService.changePassword(request);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
}