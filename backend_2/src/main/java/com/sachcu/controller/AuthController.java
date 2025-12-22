package com.sachcu.controller;

import com.sachcu.dto.request.LoginRequest;
import com.sachcu.dto.request.RegisterRequest;
import com.sachcu.dto.response.AuthResponse;
import com.sachcu.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller: AuthController
 * Mô tả: Xử lý các API liên quan đến xác thực
 * 
 * APIs:
 * - POST /auth/register - Đăng ký tài khoản User
 * - POST /auth/login - Đăng nhập User
 * - POST /auth/admin/login - Đăng nhập Admin
 * 
 * BCrypt được sử dụng để mã hóa mật khẩu người dùng.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * API: Đăng ký tài khoản User
     * Method: POST
     * Endpoint: /auth/register
     * Auth: Không cần
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Đăng nhập User
     * Method: POST
     * Endpoint: /auth/login
     * Auth: Không cần
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Đăng nhập Admin
     * Method: POST
     * Endpoint: /auth/admin/login
     * Auth: Không cần
     */
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.adminLogin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}