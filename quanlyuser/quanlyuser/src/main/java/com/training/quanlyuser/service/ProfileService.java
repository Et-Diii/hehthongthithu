package com.training.quanlyuser.service;

import com.training.quanlyuser.dto.request.ChangePasswordRequest;
import com.training.quanlyuser.dto.request.UpdateProfileRequest;
import com.training.quanlyuser.dto.response.ProfileResponse;


//hà hồng dương -17/3/2026
public interface ProfileService {

    ProfileResponse getProfile();

    void updateProfile(UpdateProfileRequest request);

    void changePassword(String username, ChangePasswordRequest request);
}
