package com.training.quanlyuser.service;

import com.training.quanlyuser.dto.request.LoginRequest;
import com.training.quanlyuser.dto.response.LoginResponse;

//hà hồng dương -12/3/2026
public interface AuthService {

    LoginResponse login(LoginRequest request);
    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

}