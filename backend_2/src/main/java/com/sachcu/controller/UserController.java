package com.sachcu.controller;

import com.sachcu.dto.request.UpdateUserRequest;
import com.sachcu.dto.response.UserResponse;
import com.sachcu.service.UserService;
import com.sachcu.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller: UserController
 * Mô tả: Xử lý các API liên quan đến User
 * 
 * APIs:
 * - GET /users/{userID} - Lấy thông tin chi tiết User (Cần đăng nhập)
 * - PUT /users/{userID} - Cập nhật thông tin User (Cần đăng nhập)
 * - POST /users/{userID}/change-password - Đổi mật khẩu (Cần đăng nhập)
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    /**
     * API: Lấy thông tin chi tiết User
     * Method: GET
     * Endpoint: /users/{userID}
     * Auth: Cần đăng nhập (ROLE_USER)
     */
    @GetMapping("/{userID}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserById(@PathVariable Integer userID,
                                         @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            Integer tokenUserId = jwtUtil.extractUserId(jwtToken);
            
            if (!tokenUserId.equals(userID)) {
                return ResponseEntity.status(403).body("Bạn không có quyền xem thông tin này");
            }
            
            UserResponse response = userService.getUserById(userID);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Cập nhật thông tin User
     * Method: PUT
     * Endpoint: /users/{userID}
     * Auth: Cần đăng nhập (ROLE_USER)
     */
    @PutMapping("/{userID}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUser(@PathVariable Integer userID,
                                        @RequestBody UpdateUserRequest request,
                                        @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            Integer tokenUserId = jwtUtil.extractUserId(jwtToken);
            
            if (!tokenUserId.equals(userID)) {
                return ResponseEntity.status(403).body("Bạn không có quyền cập nhật thông tin này");
            }
            
            UserResponse response = userService.updateUser(userID, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Đổi mật khẩu
     * Method: POST
     * Endpoint: /users/{userID}/change-password
     * Auth: Cần đăng nhập (ROLE_USER)
     */
    @PostMapping("/{userID}/change-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@PathVariable Integer userID,
                                           @RequestBody Map<String, String> request,
                                           @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            Integer tokenUserId = jwtUtil.extractUserId(jwtToken);
            
            if (!tokenUserId.equals(userID)) {
                return ResponseEntity.status(403).body("Bạn không có quyền đổi mật khẩu này");
            }
            
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Vui lòng cung cấp đầy đủ thông tin");
            }
            
            userService.changePassword(userID, oldPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}