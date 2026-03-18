package com.training.quanlyuser.controller;

import com.training.quanlyuser.dto.request.ChangePasswordRequest;
import com.training.quanlyuser.dto.request.UpdateProfileRequest;
import com.training.quanlyuser.dto.response.ProfileResponse;
import com.training.quanlyuser.entity.User;
import com.training.quanlyuser.repository.UserRepository;
import com.training.quanlyuser.security.JwtUtil;
import com.training.quanlyuser.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // Phải có cái này để file HTML (khác port) gọi được API
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // Lấy thông tin profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            // Kiểm tra an toàn cho header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc bị thiếu");
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ProfileResponse res = new ProfileResponse();
            res.setUsername(user.getUsername());
            res.setEmail(user.getEmail());
            res.setFullName(user.getFullName());
            res.setPhone(user.getPhone());
            res.setAddress(user.getAddress());
            res.setSchool(user.getSchool());
            res.setDateOfBirth(user.getDob());
            res.setAvatar(user.getAvatarUrl());

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }

    // Update thông tin cá nhân
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(HttpServletRequest request, @RequestBody UpdateProfileRequest req) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc bị thiếu");
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setFullName(req.getFullName());
            user.setEmail(req.getEmail());
            user.setPhone(req.getPhone());
            user.setAddress(req.getAddress());
            user.setDob(req.getDateOfBirth());
            user.setSchool(req.getSchool());

            userRepository.save(user);

            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }

    // Đổi mật khẩu
    @PutMapping("/profile/change-password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest req) {
        try {
            // Lấy token từ header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }

            // Giải mã token lấy username
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            // Truyền username xuống Service
            profileService.changePassword(username, req);

            return ResponseEntity.ok("Đổi mật khẩu thành công");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}