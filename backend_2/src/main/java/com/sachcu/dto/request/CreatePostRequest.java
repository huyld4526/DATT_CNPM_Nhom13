package com.sachcu.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO Request: CreatePostRequest
 * Mô tả: Yêu cầu tạo bài đăng bán sách
 * Sử dụng: POST /posts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    
    // Thông tin sách
    @NotBlank(message = "Tên sách không được để trống")
    private String title;
    
    private String author;
    
    @NotBlank(message = "Tình trạng sách không được để trống")
    private String bookCondition;
    
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;
    
    @NotBlank(message = "Mô tả không được để trống")
    private String postDescription;
    
    private String image;
    
    @NotBlank(message = "Thông tin liên hệ không được để trống")
    private String contactInfo;
    
    @NotNull(message = "Danh mục không được để trống")
    private Integer categoryID;
    
    private String province;
    private String district;
}