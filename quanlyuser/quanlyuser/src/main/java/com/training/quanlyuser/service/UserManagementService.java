package com.training.quanlyuser.service;

import com.training.quanlyuser.dto.request.UserRequest;
import com.training.quanlyuser.dto.response.UserResponse;
import com.training.quanlyuser.entity.Role;
import com.training.quanlyuser.entity.User;
import com.training.quanlyuser.repository.RoleRepository;
import com.training.quanlyuser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Role getRoleFromName(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            throw new RuntimeException("Role not provided");
        }
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    }

    // Thêm user mới
    public UserResponse addUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        // 1. Xử lý Role (Quyền)
        String roleStr = request.getRole();
        if (roleStr == null || roleStr.trim().isEmpty()) {
            user.setRole(getRoleFromName("STUDENT")); // Đảm bảo "STUDENT" có tồn tại trong bảng Role
        } else {
            user.setRole(getRoleFromName(roleStr));
        }

        // 2. Xử lý Mật khẩu (Password)
        String rawPassword = request.getPassword();
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            rawPassword = "123456"; // Mật khẩu mặc định nếu bỏ trống
        }
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        // 3. Xử lý Trạng thái (Status) - ĐÂY LÀ ĐOẠN FIX LỖI VỪA RỒI
        // Gán trạng thái mặc định là kích hoạt (ví dụ: 1).
        // *Lưu ý: Nếu Entity User của bạn dùng kiểu String ("ACTIVE") hoặc Boolean (true), hãy sửa lại số 1 cho khớp nhé.
        user.setStatus("ACTIVE");

        // Lưu xuống Database
        user = userRepository.save(user);

        return mapToResponse(user);
    }

    // Cập nhật user
    public UserResponse updateUser(Long id, UserRequest request) {
        // Tìm user hiện tại trong database
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // 1. Cập nhật các thông tin cơ bản
        existing.setUsername(request.getUsername());
        existing.setEmail(request.getEmail());
        existing.setFullName(request.getFullName());


        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            existing.setPhone(request.getPhone());
        }

        // 2. Cập nhật Role (Quyền)
        String roleStr = request.getRole();

        // In ra console để kiểm tra xem Frontend có thực sự gửi Role lên không
        System.out.println("=== KIỂM TRA ROLE GỬI LÊN: " + roleStr + " ===");

        if (roleStr != null && !roleStr.trim().isEmpty()) {
            existing.setRole(getRoleFromName(roleStr));
        }

        // 3. Cập nhật Mật khẩu (Password)
        String newPassword = request.getPassword();
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            existing.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        // 4. Cập nhật Trạng thái (Status/State)
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }

        // Lưu những thay đổi xuống Database
        existing = userRepository.save(existing);

        return mapToResponse(existing);
    }

    // Lấy tất cả user
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Lấy user theo id
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    // Xóa user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    // Mapping User -> UserResponse
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole() != null ? user.getRole().getName() : null);
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());
        return response;
    }
}