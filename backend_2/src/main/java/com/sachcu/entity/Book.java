package com.sachcu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity: Book
 * Mô tả: Thông tin sách
 * Bảng: book
 */
@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookID;
    
    @Column(nullable = false, length = 150)
    private String title;
    
    @Column(length = 100)
    private String author;
    
    @Column(length = 50)
    private String bookCondition;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 255)
    private String image;
    
    @Column(length = 100)
    private String contactInfo;
    
    @Column(length = 50)
    private String province;
    
    @Column(length = 50)
    private String district;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL)
    private Post post;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCategory> bookCategories = new ArrayList<>();
}