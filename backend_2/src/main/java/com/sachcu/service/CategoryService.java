package com.sachcu.service;

import com.sachcu.dto.response.CategoryResponse;
import com.sachcu.entity.Category;
import com.sachcu.exception.ResourceNotFoundException;
import com.sachcu.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service: CategoryService
 * Mô tả: Xử lý logic liên quan đến Category
 * APIs:
 * - GET /categories - Lấy tất cả danh mục
 * - POST /admin/categories - Thêm danh mục mới (Admin)
 * - PUT /admin/categories/{categoryID} - Sửa danh mục (Admin)
 * - DELETE /admin/categories/{categoryID} - Xóa danh mục (Admin)
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Lấy tất cả danh mục
     */
    public List<CategoryResponse> getAllCategories() {
    return categoryRepository.findAll()
        .stream()
        .map(cat -> new CategoryResponse(
                cat.getCategoryID(),
                cat.getCategoryName(),
                cat.getBookCategories().size()
        ))
        .toList();
    }

    
    /**
     * Lấy Category theo ID
     */
    public Category getCategoryById(Integer categoryID) {
        return categoryRepository.findById(categoryID)
                .orElseThrow(() -> new ResourceNotFoundException("Category không tồn tại với ID: " + categoryID));
    }
    
    /**
     * Thêm danh mục mới (Admin)
     */
    @Transactional
    public Category createCategory(String categoryName) {
        // Kiểm tra tên danh mục đã tồn tại
        if (categoryRepository.findByCategoryName(categoryName).isPresent()) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }
        
        Category category = new Category();
        category.setCategoryName(categoryName);
        return categoryRepository.save(category);
    }
    
    /**
     * Cập nhật danh mục (Admin)
     */
    @Transactional
    public Category updateCategory(Integer categoryID, String categoryName) {
        Category category = getCategoryById(categoryID);
        
        // Kiểm tra tên danh mục mới có trùng với danh mục khác không
        categoryRepository.findByCategoryName(categoryName).ifPresent(existingCategory -> {
            if (!existingCategory.getCategoryID().equals(categoryID)) {
                throw new RuntimeException("Tên danh mục đã tồn tại");
            }
        });
        
        category.setCategoryName(categoryName);
        return categoryRepository.save(category);
    }
    
    /**
     * Xóa danh mục (Admin)
     */
    @Transactional
    public void deleteCategory(Integer categoryID) {
        Category category = getCategoryById(categoryID);
        categoryRepository.delete(category);
    }
}