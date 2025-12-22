package com.sachcu.repository;

import com.sachcu.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository: BookRepository
 * Mô tả: Truy vấn dữ liệu bảng Book
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    
    // Tìm kiếm sách theo tiêu đề
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    // Tìm kiếm sách theo khu vực
    List<Book> findByProvince(String province);
    
    // Tìm kiếm sách theo tác giả
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    // Tìm kiếm sách nâng cao
       @Query("SELECT b FROM Book b WHERE " +
              "(:title IS NULL OR :title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
              "(:author IS NULL OR :author = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
              "(:province IS NULL OR :province = '' OR b.province = :province) AND " +
              "(:district IS NULL OR :district = '' OR b.district = :district)")
       List<Book> searchBooks(@Param("title") String title,
                            @Param("author") String author,
                            @Param("province") String province,
                            @Param("district") String district);

}