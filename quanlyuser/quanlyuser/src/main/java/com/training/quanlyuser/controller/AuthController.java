package com.training.quanlyuser.controller;

import com.training.quanlyuser.dto.request.ForgotPasswordRequest;
import com.training.quanlyuser.dto.request.LoginRequest;
import com.training.quanlyuser.dto.request.RegisterRequest;
import com.training.quanlyuser.dto.request.ResetPasswordRequest;
import com.training.quanlyuser.dto.response.LoginResponse;
import com.training.quanlyuser.service.AuthService;

import com.training.quanlyuser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//hà hồng dương -12/3/2026
@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        System.out.println("Username: " + request.getUsername());
        System.out.println("Password: " + request.getPassword());

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Đã gửi email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }

}