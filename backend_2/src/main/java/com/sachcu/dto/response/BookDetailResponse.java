package com.sachcu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO Response: BookDetailResponse
 * Mô tả: Chi tiết sách kèm thông tin bài đăng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailResponse {
    
    // Book info
    private Integer bookID;
    private String title;
    private String author;
    private String bookCondition;
    private BigDecimal price;
    private String description;
    private String image;
    private String contactInfo; // Sẽ bị ẩn nếu guest
    private String province;
    private String district;
    private LocalDateTime createdAt;
    
    // Post info
    private Integer postID;
    private String postDescription;
    private String postStatus;
    
    // User info
    private Integer userID;
    private String userName;
    
    // Category
    private Integer categoryID;
    private String categoryName;
}