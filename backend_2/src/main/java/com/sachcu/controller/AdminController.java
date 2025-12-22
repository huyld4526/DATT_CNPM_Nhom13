package com.sachcu.controller;

import com.sachcu.dto.response.PostResponse;
import com.sachcu.dto.response.UserResponse;
import com.sachcu.dto.response.UserStatusResponse;
import com.sachcu.entity.Post;
import com.sachcu.entity.Report;
import com.sachcu.entity.User;
import com.sachcu.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller: AdminController
 * Mô tả: Xử lý các API dành cho Admin
 * 
 * APIs:
 * - GET /admin/posts - Lấy tất cả bài đăng (Admin)
 * - GET /admin/posts/status/{status} - Lấy bài đăng theo trạng thái (Admin)
 * - PUT /admin/posts/{postID}/status - Duyệt/từ chối bài đăng (Admin)
 * - GET /admin/users - Lấy danh sách User (Admin)
 * - PUT /admin/users/{userID}/status - Cập nhật trạng thái User (Admin)
 * - DELETE /admin/users/{userID} - Xóa User (Admin)
 * - GET /admin/reports - Lấy danh sách báo cáo (Admin)
 * - PUT /admin/reports/{reportID}/status - Xử lý báo cáo (Admin)
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final AdminService adminService;
    
    /**
     * API: Lấy tất cả bài đăng
     * Method: GET
     * Endpoint: /admin/posts
     * Auth: ROLE_ADMIN
     */
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        try {
            List<PostResponse> posts = adminService.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Lấy bài đăng theo trạng thái
     * Method: GET
     * Endpoint: /admin/posts/status/{status}
     * Auth: ROLE_ADMIN
     */
    @GetMapping("/posts/status/{status}")
    public ResponseEntity<?> getPostsByStatus(@PathVariable String status) {
        try {
            Post.PostStatus postStatus = Post.PostStatus.valueOf(status.toUpperCase());
            List<PostResponse> posts = adminService.getPostsByStatus(postStatus);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ: " + status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Duyệt hoặc từ chối bài đăng
     * Method: PUT
     * Endpoint: /admin/posts/{postID}/status
     * Auth: ROLE_ADMIN
     * Body: { "status": "APPROVED" | "DECLINED" | "SOLD" }
     */
    @PutMapping("/posts/{postID}/status")
    public ResponseEntity<?> updatePostStatus(@PathVariable Integer postID,
                                              @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Trạng thái không được để trống");
            }
            
            PostResponse response = adminService.updatePostStatus(postID, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Lấy danh sách tất cả User
     * Method: GET
     * Endpoint: /admin/users
     * Auth: ROLE_ADMIN
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    

    /**
     * API: Lấy thông tin User theo ID (Admin)
     * Method: GET
     * Endpoint: /admin/users/{id}
     * Auth: ROLE_ADMIN
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            UserResponse response = adminService.getUserById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    /**
     * API: Cập nhật trạng thái User
     * Method: PUT
     * Endpoint: /admin/users/{userID}/status
     * Auth: ROLE_ADMIN
     * Body: { "status": "ACTIVE" | "SUSPENDED" | "BANNED" | "DELETED" }
     */
    @PutMapping("/users/{userID}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Integer userID,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");
        if (status == null)
            return ResponseEntity.badRequest().body("Trạng thái không được để trống");

        return ResponseEntity.ok(adminService.updateUserStatus(userID, status));
    }



    
    /**
     * API: Xóa User
     * Method: DELETE
     * Endpoint: /admin/users/{userID}
     * Auth: ROLE_ADMIN
     */
    @DeleteMapping("/users/{userID}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userID) {
        try {
            adminService.deleteUser(userID);
            return ResponseEntity.ok("Xóa User thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Lấy tất cả báo cáo
     * Method: GET
     * Endpoint: /admin/reports
     * Auth: ROLE_ADMIN
     */
    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports() {
        try {
            List<Report> reports = adminService.getAllReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Xử lý báo cáo
     * Method: PUT
     * Endpoint: /admin/reports/{reportID}/status
     * Auth: ROLE_ADMIN
     * Body: { "status": "RESOLVED" | "DISMISSED" }
     */
    @PutMapping("/reports/{reportID}/status")
    public ResponseEntity<?> updateReportStatus(@PathVariable Integer reportID,
                                                @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Trạng thái không được để trống");
            }
            
            Report report = adminService.updateReportStatus(reportID, status);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}