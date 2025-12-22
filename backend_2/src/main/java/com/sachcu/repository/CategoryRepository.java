package com.sachcu.repository;

import com.sachcu.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository: CategoryRepository
 * Mô tả: Truy vấn dữ liệu bảng Category
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    Optional<Category> findByCategoryName(String categoryName);
}