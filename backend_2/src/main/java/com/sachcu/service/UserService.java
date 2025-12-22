package com.sachcu.service;

import com.sachcu.dto.request.UpdateUserRequest;
import com.sachcu.dto.response.UserResponse;
import com.sachcu.entity.User;
import com.sachcu.exception.ResourceNotFoundException;
import com.sachcu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service: UserService
 * Mô tả: Xử lý logic liên quan đến User
 * APIs:
 * - GET /users/{userID} - Lấy thông tin chi tiết User
 * - PUT /users/{userID} - Cập nhật thông tin User
 * - POST /users/{userID}/change-password - Đổi mật khẩu
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Lấy thông tin User theo ID
     */
    public UserResponse getUserById(Integer userID) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + userID));
        
        return convertToResponse(user);
    }
    
    /**
     * Cập nhật thông tin User
     */
    @Transactional
    public UserResponse updateUser(Integer userID, UpdateUserRequest request) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + userID));
        
        // Cập nhật thông tin
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getProvince() != null) {
            user.setProvince(request.getProvince());
        }
        if (request.getDistrict() != null) {
            user.setDistrict(request.getDistrict());
        }
        if (request.getWard() != null) {
            user.setWard(request.getWard());
        }
        
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }
    
    /**
     * Đổi mật khẩu User
     */
    @Transactional
    public void changePassword(Integer userID, String oldPassword, String newPassword) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + userID));
        
        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }
        
        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Convert User entity sang UserResponse
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserID(user.getUserID());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setProvince(user.getProvince());
        response.setDistrict(user.getDistrict());
        response.setWard(user.getWard());
        response.setStatus(user.getStatus().name());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}