package com.training.quanlyuser.service;

import com.training.quanlyuser.dto.request.LoginRequest;
import com.training.quanlyuser.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

}