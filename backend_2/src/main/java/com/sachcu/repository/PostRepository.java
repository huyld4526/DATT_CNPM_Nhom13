package com.sachcu.repository;

import com.sachcu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository: PostRepository
 * Mô tả: Truy vấn dữ liệu bảng Posts
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    
    // Lấy tất cả bài đăng của một user
    List<Post> findByUser_UserID(Integer userID);
    
    // Lấy bài đăng theo trạng thái
    List<Post> findByStatus(Post.PostStatus status);
    
    // Lấy bài đăng đã duyệt
    List<Post> findByStatusOrderByCreatedAtDesc(Post.PostStatus status);
}