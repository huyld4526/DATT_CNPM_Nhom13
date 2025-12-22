package com.sachcu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Request: UpdateUserRequest
 * Mô tả: Yêu cầu cập nhật thông tin user
 * Sử dụng: PUT /users/{userID}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    private String name;
    private String phone;
    private String province;
    private String district;
    private String ward;
}