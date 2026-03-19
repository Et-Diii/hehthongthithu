package com.training.quanlyuser.service;


import com.training.quanlyuser.dto.request.RegisterRequest;
import com.training.quanlyuser.entity.*;
import com.training.quanlyuser.repository.RoleRepository;
import com.training.quanlyuser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


//hà hồng dương -17/3/2026
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public String register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setSchool(request.getSchool());
        user.setDob(request.getDate());
        user.setStatus("ACTIVE");

        // 🔥 ROLE MẶC ĐỊNH STUDENT
        Role role = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Role STUDENT không tồn tại"));

        user.setRole(role);

        userRepository.save(user);

        return "Đăng ký thành công";
    }
}