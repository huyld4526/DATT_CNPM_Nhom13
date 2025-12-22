package com.sachcu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO Response: PostResponse
 * Mô tả: Thông tin bài đăng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    
    private Integer postID;
    private Integer bookID;
    private String title;
    private String author;
    private BigDecimal price;
    private String image;
    private String province;
    private String district;
    private String postStatus;
    private LocalDateTime createdAt;
}