package com.sachcu.controller;

import com.sachcu.dto.request.CreatePostRequest;
import com.sachcu.dto.request.UpdatePostRequest;
import com.sachcu.dto.response.PostResponse;
import com.sachcu.dto.response.BookDetailResponse;
import com.sachcu.service.PostService;
import com.sachcu.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller: PostController
 * Mô tả: Xử lý các API liên quan đến Post (Bài đăng)
 * 
 * ==================== PUBLIC APIs (Không cần đăng nhập) ====================
 * - GET /posts/{postID} - Xem chi tiết bài đăng (ẩn thông tin nếu chưa login)
 * 
 * ==================== USER APIs (Cần đăng nhập) ====================
 * - POST /posts - Đăng bài bán sách mới
 * - GET /my-posts - Xem tất cả bài đăng của chính mình
 * - PUT /my-posts/{postID} - Sửa bài đăng của chính mình
 * - DELETE /my-posts/{postID} - Xóa bài đăng của chính mình
 * - PUT /my-posts/{postID}/sold - Đánh dấu đã bán
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {
    
    private final PostService postService;
    private final JwtUtil jwtUtil;
    
    // ========================================================================
    // PUBLIC APIs - KHÔNG CẦN ĐĂNG NHẬP
    // ========================================================================
    
    /**
     * API: Xem chi tiết bài đăng
     * Method: GET
     * Endpoint: /posts/{postID}
     * Auth: KHÔNG CẦN (Public)
     * 
     * Chức năng:
     * - Guest: Xem được bài đăng nhưng ẨN thông tin liên hệ và người đăng
     * - User đã login: Xem được ĐẦY ĐỦ thông tin
     */
    @GetMapping("/posts/{postID}")
    public ResponseEntity<?> getPostDetail(
            @PathVariable Integer postID,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // Kiểm tra user đã đăng nhập hay chưa
            boolean isAuthenticated = (token != null && token.startsWith("Bearer "));
            
            BookDetailResponse response = postService.getPostDetail(postID, isAuthenticated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
    
    // ========================================================================
    // USER APIs - CẦN ĐĂNG NHẬP
    // ========================================================================
    
    /**
     * API: Đăng bài bán sách mới
     * Method: POST
     * Endpoint: /posts
     * Auth: CẦN đăng nhập (ROLE_USER)
     * 
     * Chức năng: User tạo bài đăng mới (gộp Book và Post)
     * Trạng thái mặc định: PENDING (chờ Admin duyệt)
     */
    @PostMapping("/posts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            // Lấy userID từ JWT token
            String jwtToken = token.substring(7);
            Integer userID = jwtUtil.extractUserId(jwtToken);
            
            PostResponse response = postService.createPost(userID, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
    
    /**
     * API: Xem tất cả bài đăng của chính mình (My Posts)
     * Method: GET
     * Endpoint: /my-posts
     * Auth: CẦN đăng nhập (ROLE_USER)
     * 
     * Chức năng: 
     * - User xem TẤT CẢ bài đăng của mình
     * - Bao gồm: PENDING, APPROVED, DECLINED, SOLD
     * - Hiển thị đầy đủ thông tin (vì là bài của chính mình)
     */
    @GetMapping("/my-posts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMyPosts(
            @RequestHeader("Authorization") String token) {
        try {
            // Lấy userID từ JWT token
            String jwtToken = token.substring(7);
            Integer userID = jwtUtil.extractUserId(jwtToken);
            
            List<BookDetailResponse> posts = postService.getMyPosts(userID);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
    
    /**
     * API: Sửa bài đăng của chính mình
     * Method: PUT
     * Endpoint: /my-posts/{postID}
     * Auth: CẦN đăng nhập (ROLE_USER)
     * 
     * Chức năng: User chỉnh sửa bài đăng của chính mình
     * 
     * Quy tắc:
     * - Chỉ sửa được bài của CHÍNH MÌNH
     * - KHÔNG sửa được bài đã SOLD
     * - Nếu bài bị DECLINED → sau khi sửa sẽ reset về PENDING
     */
    @PutMapping("/my-posts/{postID}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateMyPost(
            @PathVariable Integer postID,
            @RequestBody UpdatePostRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            // Lấy userID từ JWT token
            String jwtToken = token.substring(7);
            Integer userID = jwtUtil.extractUserId(jwtToken);
            
            // Service sẽ kiểm tra quyền sở hữu
            BookDetailResponse response = postService.updateMyPost(postID, userID, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
    
    /**
     * API: Xóa bài đăng của chính mình
     * Method: DELETE
     * Endpoint: /my-posts/{postID}
     * Auth: CẦN đăng nhập (ROLE_USER)
     * 
     * Chức năng: User xóa bài đăng của chính mình
     * 
     * Quy tắc:
     * - Chỉ xóa được bài của CHÍNH MÌNH
     * - KHÔNG xóa được bài đã APPROVED (phải liên hệ Admin)
     * - Chỉ xóa được bài PENDING hoặc DECLINED
     */
    @DeleteMapping("/my-posts/{postID}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteMyPost(
            @PathVariable Integer postID,
            @RequestHeader("Authorization") String token) {
        try {
            // Lấy userID từ JWT token
            String jwtToken = token.substring(7);
            Integer userID = jwtUtil.extractUserId(jwtToken);
            
            // Service sẽ kiểm tra quyền sở hữu
            postService.deleteMyPost(postID, userID);
            return ResponseEntity.ok(Map.of(
                "message", "Xóa bài đăng thành công",
                "postID", postID,
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
    
    /**
     * API: Đánh dấu bài đăng đã bán
     * Method: PUT
     * Endpoint: /my-posts/{postID}/sold
     * Auth: CẦN đăng nhập (ROLE_USER)
     * 
     * Chức năng: 
     * - User đánh dấu bài đăng của mình đã bán
     * - Trạng thái sẽ chuyển sang SOLD
     * - Bài SOLD sẽ không hiển thị trong danh sách sách công khai nữa
     */
    @PutMapping("/my-posts/{postID}/sold")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> markAsSold(
            @PathVariable Integer postID,
            @RequestHeader("Authorization") String token) {
        try {
            // Lấy userID từ JWT token
            String jwtToken = token.substring(7);
            Integer userID = jwtUtil.extractUserId(jwtToken);
            
            // Service sẽ kiểm tra quyền sở hữu
            postService.markAsSold(postID, userID);
            return ResponseEntity.ok(Map.of(
                "message", "Đã đánh dấu bài đăng là đã bán",
                "postID", postID,
                "status", "SOLD",
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
}