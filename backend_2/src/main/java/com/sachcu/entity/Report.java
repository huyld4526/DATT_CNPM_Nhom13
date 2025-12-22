package com.sachcu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity: Report
 * Mô tả: Báo cáo vi phạm bài đăng
 * Bảng: report
 */
@Entity
@Table(name = "report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportID;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postID", nullable = false)
    private Post post;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminID")
    private Admin admin;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime reportDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.OPEN;
    
    public enum ReportStatus {
        OPEN, RESOLVED, DISMISSED
    }
}