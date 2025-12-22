package com.sachcu.repository;

import com.sachcu.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository: ReportRepository
 * Mô tả: Truy vấn dữ liệu bảng Report
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    
    // Lấy báo cáo theo trạng thái
    List<Report> findByStatus(Report.ReportStatus status);
    
    // Lấy báo cáo theo bài đăng
    List<Report> findByPost_PostID(Integer postID);
}