package com.sachcu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Response: AuthResponse
 * Mô tả: Phản hồi sau khi đăng nhập/đăng ký thành công
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private Integer userID;
    private String name;
    private String email;
    private String role; // USER hoặc ADMIN
    
    public AuthResponse(String token, Integer userID, String name, String email, String role) {
        this.token = token;
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}