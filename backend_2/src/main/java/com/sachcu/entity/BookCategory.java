package com.sachcu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Entity: BookCategory
 * Mô tả: Bảng trung gian giữa Book và Category
 * Bảng: book_category
 */
@Entity
@Table(name = "book_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCategory {
    
    @EmbeddedId
    private BookCategoryId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookID")
    @JoinColumn(name = "bookID")
    private Book book;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryID")
    @JoinColumn(name = "categoryID")
    private Category category;
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookCategoryId implements Serializable {
        private Integer bookID;
        private Integer categoryID;
    }
}