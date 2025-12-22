package com.sachcu.service;

import com.sachcu.dto.response.PostResponse;
import com.sachcu.dto.response.UserResponse;
import com.sachcu.entity.Post;
import com.sachcu.entity.Report;
import com.sachcu.entity.User;
import com.sachcu.exception.ResourceNotFoundException;
import com.sachcu.repository.PostRepository;
import com.sachcu.repository.ReportRepository;
import com.sachcu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service: AdminService
 * Mô tả: Xử lý logic dành cho Admin
 * APIs:
 * - GET /admin/posts - Lấy tất cả bài đăng (Admin)
 * - PUT /admin/posts/{postID}/status - Duyệt/từ chối bài đăng (Admin)
 * - GET /admin/users - Quản lý danh sách User (Admin)
 * - PUT /admin/users/{userID} - Cập nhật trạng thái User (Admin)
 * - DELETE /admin/users/{userID} - Xóa User (Admin)
 * - GET /admin/reports - Xem danh sách báo cáo (Admin)
 * - PUT /admin/reports/{reportID} - Xử lý báo cáo (Admin)
 */
@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    
    /**
     * Lấy tất cả bài đăng (Admin)
     */
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy bài đăng theo trạng thái (Admin)
     */
    public List<PostResponse> getPostsByStatus(Post.PostStatus status) {
        return postRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Duyệt hoặc từ chối bài đăng (Admin)
     */
    @Transactional
    public PostResponse updatePostStatus(Integer postID, String status) {
        Post post = postRepository.findById(postID)
                .orElseThrow(() -> new ResourceNotFoundException("Bài đăng không tồn tại"));
        
        try {
            Post.PostStatus newStatus = Post.PostStatus.valueOf(status.toUpperCase());
            post.setStatus(newStatus);
            Post updatedPost = postRepository.save(post);
            return convertToResponse(updatedPost);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }
    }
    
    /**
     * Lấy danh sách tất cả User (Admin)
     */
    public List<UserResponse> getAllUsers() {
    List<User> users = userRepository.findAll();

    return users.stream()
            .map(user -> new UserResponse(
                    user.getUserID(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getProvince(),
                    user.getDistrict(),
                    user.getWard(),
                    user.getStatus().name(),
                    user.getCreatedAt()
            ))
            .toList();
    }


    /**
     * Lấy thông tin User theo ID (Admin)
     */
    public UserResponse getUserById(Integer id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User không tồn tại"));

    return new UserResponse(
            user.getUserID(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getProvince(),
            user.getDistrict(),
            user.getWard(),
            user.getStatus().name(),
            user.getCreatedAt()
        );
    }



    
    /**
     * Cập nhật trạng thái User (Admin)
     */
    @Transactional
    public UserResponse updateUserStatus(Integer userID, String status) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        User.UserStatus newStatus = User.UserStatus.valueOf(status.toUpperCase());
        user.setStatus(newStatus);
        userRepository.save(user);

        return new UserResponse(
                user.getUserID(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getProvince(),
                user.getDistrict(),
                user.getWard(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }

    
    /**
     * Xóa User (Admin)
     */
    @Transactional
    public void deleteUser(Integer userID) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));
        userRepository.delete(user);
    }
    
    /**
     * Lấy tất cả báo cáo (Admin)
     */
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
    
    /**
     * Lấy báo cáo theo trạng thái (Admin)
     */
    public List<Report> getReportsByStatus(Report.ReportStatus status) {
        return reportRepository.findByStatus(status);
    }
    
    /**
     * Xử lý báo cáo (Admin)
     */
    @Transactional
    public Report updateReportStatus(Integer reportID, String status) {
        Report report = reportRepository.findById(reportID)
                .orElseThrow(() -> new ResourceNotFoundException("Báo cáo không tồn tại"));
        
        try {
            Report.ReportStatus newStatus = Report.ReportStatus.valueOf(status.toUpperCase());
            report.setStatus(newStatus);
            return reportRepository.save(report);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }
    }
    
    /**
     * Convert Post entity sang PostResponse
     */
    private PostResponse convertToResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setPostID(post.getPostID());
        response.setPostStatus(post.getStatus().name());
        response.setCreatedAt(post.getCreatedAt());
        
        if (post.getBook() != null) {
            response.setBookID(post.getBook().getBookID());
            response.setTitle(post.getBook().getTitle());
            response.setAuthor(post.getBook().getAuthor());
            response.setPrice(post.getBook().getPrice());
            response.setImage(post.getBook().getImage());
            response.setProvince(post.getBook().getProvince());
            response.setDistrict(post.getBook().getDistrict());
        }
        
        return response;
    }
}