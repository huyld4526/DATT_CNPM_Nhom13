package com.sachcu.controller;

import com.sachcu.dto.response.BookDetailResponse;
import com.sachcu.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller: BookController
 * Mô tả: Xử lý các API liên quan đến Book (Public - không cần đăng nhập)
 * 
 * APIs:
 * - GET /books - Lấy danh sách tất cả sách đã duyệt (Public, ẩn thông tin)
 * - GET /books/{bookID} - Xem chi tiết sách (Public, ẩn contact nếu chưa login)
 * - GET /books/search - Tìm kiếm sách (Public, ẩn thông tin)
 * - GET /books/province/{province} - Lấy sách theo tỉnh (Public, ẩn thông tin)
 */
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookController {
    
    private final BookService bookService;
    
    /**
     * API: Lấy danh sách tất cả sách đã duyệt
     * Method: GET
     * Endpoint: /books
     * Auth: KHÔNG CẦN (Public)
     * Note: Ẩn thông tin liên hệ và người đăng nếu chưa login
     */
    @GetMapping
    public ResponseEntity<?> getAllBooks(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            boolean isAuthenticated = (token != null && token.startsWith("Bearer "));
            List<BookDetailResponse> books = bookService.getAllApprovedBooks(isAuthenticated);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Xem chi tiết sách
     * Method: GET
     * Endpoint: /books/{bookID}
     * Auth: KHÔNG CẦN (Public)
     * Note: Ẩn thông tin liên hệ và người đăng nếu chưa login
     */
    @GetMapping("/{bookID}")
    public ResponseEntity<?> getBookDetail(@PathVariable Integer bookID,
                                          @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            boolean isAuthenticated = (token != null && token.startsWith("Bearer "));
            BookDetailResponse book = bookService.getBookDetail(bookID, isAuthenticated);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Tìm kiếm sách theo nhiều tiêu chí
     * Method: GET
     * Endpoint: /books/search?title=xxx&author=xxx&province=xxx&district=xxx
     * Auth: KHÔNG CẦN (Public)
     * Note: Ẩn thông tin liên hệ và người đăng nếu chưa login
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam(required = false) String title,
                                        @RequestParam(required = false) String author,
                                        @RequestParam(required = false) String province,
                                        @RequestParam(required = false) String district,
                                        @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            boolean isAuthenticated = (token != null && token.startsWith("Bearer "));
            List<BookDetailResponse> books = bookService.searchBooks(title, author, province, district, isAuthenticated);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API: Lấy sách theo tỉnh/thành phố
     * Method: GET
     * Endpoint: /books/province/{province}
     * Auth: KHÔNG CẦN (Public)
     * Note: Ẩn thông tin liên hệ và người đăng nếu chưa login
     */
    @GetMapping("/province/{province}")
    public ResponseEntity<?> getBooksByProvince(@PathVariable String province,
                                                @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            boolean isAuthenticated = (token != null && token.startsWith("Bearer "));
            List<BookDetailResponse> books = bookService.getBooksByProvince(province, isAuthenticated);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}