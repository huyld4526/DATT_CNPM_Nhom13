package com.sachcu.service;

import com.sachcu.dto.request.LoginRequest;
import com.sachcu.dto.request.RegisterRequest;
import com.sachcu.dto.response.AuthResponse;
import com.sachcu.entity.Admin;
import com.sachcu.entity.User;
import com.sachcu.repository.AdminRepository;
import com.sachcu.repository.UserRepository;
import com.sachcu.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service: AuthService
 * Mô tả: Xử lý logic đăng ký, đăng nhập
 * APIs:
 * - POST /auth/register - Đăng ký tài khoản User
 * - POST /auth/login - Đăng nhập User
 * - POST /auth/admin/login - Đăng nhập Admin
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Đăng ký tài khoản User mới
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        
        // Tạo User mới
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setProvince(request.getProvince());
        user.setDistrict(request.getDistrict());
        user.setWard(request.getWard());
        user.setStatus(User.UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        // Tạo JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail(), "USER", savedUser.getUserID());
        
        return new AuthResponse(
            token,
            savedUser.getUserID(),
            savedUser.getName(),
            savedUser.getEmail(),
            "USER"
        );
    }
    
    /**
     * Đăng nhập User
     */
    public AuthResponse login(LoginRequest request) {
        // Xác thực
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        // Lấy thông tin user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        // Kiểm tra trạng thái tài khoản
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("Tài khoản đã bị khóa hoặc vô hiệu hóa");
        }
        
        // Tạo JWT token
        String token = jwtUtil.generateToken(user.getEmail(), "USER", user.getUserID());
        
        return new AuthResponse(
            token,
            user.getUserID(),
            user.getName(),
            user.getEmail(),
            "USER"
        );
    }
    
    /**
     * Đăng nhập Admin
     */
    public AuthResponse adminLogin(LoginRequest request) {
        // Xác thực
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        // Lấy thông tin admin
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin không tồn tại"));
        
        // Tạo JWT token
        String token = jwtUtil.generateToken(admin.getEmail(), "ADMIN", admin.getAdminID());
        
        return new AuthResponse(
            token,
            admin.getAdminID(),
            admin.getName(),
            admin.getEmail(),
            "ADMIN"
        );
    }
}