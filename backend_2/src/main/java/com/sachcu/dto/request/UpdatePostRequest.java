package com.sachcu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO Request: UpdatePostRequest
 * Mô tả: Yêu cầu cập nhật bài đăng
 * Sử dụng: PUT /posts/{postID}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {
    
    private String title;
    private String author;
    private String bookCondition;
    private BigDecimal price;
    private String postDescription;
    private String image;
    private String contactInfo;
    private Integer categoryID;
    private String province;
    private String district;
}