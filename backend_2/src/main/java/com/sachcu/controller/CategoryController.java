package com.sachcu.controller;

import com.sachcu.entity.Category;
import com.sachcu.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller: CategoryController
 * Mô tả: Xử lý các API liên quan đến Category
 * 
 * APIs:
 * - GET /categories - Lấy tất cả danh mục (Public)
 * - POST /admin/categories - Thêm danh mục mới (Admin)
 * - PUT /admin/categories/{categoryID} - Sửa danh mục (Admin)
 * - DELETE /admin/categories/{categoryID} - Xóa danh mục (Admin)
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * API: Lấy tất cả danh mục
     * Method: GET
     * Endpoint: /categories
     * Auth: KHÔNG CẦN (Public)
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    
    /**
     * API: Thêm danh mục mới
     * Method: POST
     * Endpoint: /admin/categories
     * Auth: Cần đăng nhập (ROLE_ADMIN)
     */
    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody Map<String, String> request) {
        try {
            String categoryName = request.get("categoryName");
            if (categoryName == null || categoryName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên danh mục không được để trống");
            }
            
            Category category = categoryService.createCategory(categoryName);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Cập nhật danh mục
     * Method: PUT
     * Endpoint: /admin/categories/{categoryID}
     * Auth: Cần đăng nhập (ROLE_ADMIN)
     */
    @PutMapping("/admin/categories/{categoryID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Integer categoryID,
                                           @RequestBody Map<String, String> request) {
        try {
            String categoryName = request.get("categoryName");
            if (categoryName == null || categoryName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên danh mục không được để trống");
            }
            
            Category category = categoryService.updateCategory(categoryID, categoryName);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Xóa danh mục
     * Method: DELETE
     * Endpoint: /admin/categories/{categoryID}
     * Auth: Cần đăng nhập (ROLE_ADMIN)
     */
    @DeleteMapping("/admin/categories/{categoryID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer categoryID) {
        try {
            categoryService.deleteCategory(categoryID);
            return ResponseEntity.ok("Xóa danh mục thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}