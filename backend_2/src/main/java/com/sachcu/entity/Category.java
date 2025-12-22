package com.sachcu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity: Category
 * Mô tả: Danh mục sách
 * Bảng: category
 */
@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryID;
    
    @Column(nullable = false, unique = true, length = 100)
    private String categoryName;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<BookCategory> bookCategories = new ArrayList<>();
}