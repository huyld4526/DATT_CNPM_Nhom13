package com.sachcu.service;

import com.sachcu.dto.request.CreatePostRequest;
import com.sachcu.dto.request.UpdatePostRequest;
import com.sachcu.dto.response.PostResponse;
import com.sachcu.dto.response.BookDetailResponse;
import com.sachcu.entity.*;
import com.sachcu.exception.ResourceNotFoundException;
import com.sachcu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service: PostService
 * Mô tả: Xử lý logic liên quan đến Post (Bài đăng bán sách)
 * 
 * Cập nhật phiên bản mới:
 * - Tích hợp FileStorageService để quản lý ảnh
 * - Tự động xóa ảnh cũ khi cập nhật/xóa bài đăng
 * - Kiểm tra quyền sở hữu chặt chẽ
 * - Ẩn thông tin liên hệ và người đăng cho Guest
 * 
 * APIs:
 * - POST /posts - Đăng bài bán sách mới (User)
 * - GET /posts/{postID} - Xem chi tiết bài đăng (Public, ẩn thông tin nếu chưa login)
 * - GET /my-posts - Xem bài đăng của chính User (User)
 * - PUT /my-posts/{postID} - Sửa bài đăng của chính User (User)
 * - DELETE /my-posts/{postID} - Xóa bài đăng của chính User (User)
 */
@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    
    /**
     * Tạo bài đăng mới
     * Transaction: Tạo Book và Post cùng lúc
     * 
     * @param userID ID của user tạo bài
     * @param request Thông tin bài đăng
     * @return PostResponse
     */
    @Transactional
    public PostResponse createPost(Integer userID, CreatePostRequest request) {
        // Lấy thông tin User
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + userID));
        
        // Kiểm tra trạng thái tài khoản
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("Tài khoản của bạn đang bị tạm khóa hoặc vô hiệu hóa");
        }
        
        // Lấy Category
        Category category = categoryRepository.findById(request.getCategoryID())
                .orElseThrow(() -> new ResourceNotFoundException("Category không tồn tại với ID: " + request.getCategoryID()));
        
        // Tạo Book
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setBookCondition(request.getBookCondition());
        book.setPrice(request.getPrice());
        book.setDescription(request.getPostDescription());
        book.setImage(request.getImage()); // URL ảnh đã upload từ /images/upload
        book.setContactInfo(request.getContactInfo());
        book.setProvince(request.getProvince());
        book.setDistrict(request.getDistrict());
        
        Book savedBook = bookRepository.save(book);
        
        // Tạo BookCategory (bảng trung gian)
        BookCategory bookCategory = new BookCategory();
        BookCategory.BookCategoryId id = new BookCategory.BookCategoryId();
        id.setBookID(savedBook.getBookID());
        id.setCategoryID(category.getCategoryID());
        bookCategory.setId(id);
        bookCategory.setBook(savedBook);
        bookCategory.setCategory(category);
        
        savedBook.getBookCategories().add(bookCategory);
        
        // Tạo Post
        Post post = new Post();
        post.setUser(user);
        post.setBook(savedBook);
        post.setDescription(request.getPostDescription());
        post.setStatus(Post.PostStatus.PENDING); // Mặc định PENDING - chờ Admin duyệt
        
        Post savedPost = postRepository.save(post);
        
        return convertToResponse(savedPost);
    }
    
    /**
     * Xem chi tiết bài đăng (Public API)
     * Guest: Ẩn thông tin liên hệ và người đăng
     * User đã login: Hiển thị đầy đủ thông tin
     * 
     * @param postID ID bài đăng
     * @param isAuthenticated User đã đăng nhập hay chưa
     * @return BookDetailResponse
     */
    public BookDetailResponse getPostDetail(Integer postID, boolean isAuthenticated) {
        Post post = postRepository.findById(postID)
                .orElseThrow(() -> new ResourceNotFoundException("Bài đăng không tồn tại với ID: " + postID));
        
        // Chỉ hiển thị bài đăng đã được duyệt
        if (post.getStatus() != Post.PostStatus.APPROVED) {
            throw new ResourceNotFoundException("Bài đăng chưa được duyệt hoặc đã bị từ chối");
        }
        
        return convertToDetailResponse(post, isAuthenticated);
    }
    
    /**
     * Lấy tất cả bài đăng của chính User
     * Hiển thị TẤT CẢ bài đăng: PENDING, APPROVED, DECLINED, SOLD
     * 
     * @param userID ID của user
     * @return List<BookDetailResponse>
     */
    public List<BookDetailResponse> getMyPosts(Integer userID) {
        List<Post> posts = postRepository.findByUser_UserID(userID);
        
        // Hiển thị đầy đủ thông tin vì là bài của chính user
        return posts.stream()
                .map(post -> convertToDetailResponse(post, true))
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật bài đăng
     * Chỉ User sở hữu mới được cập nhật
     * Tự động xóa ảnh cũ nếu upload ảnh mới
     * 
     * @param postID ID bài đăng
     * @param userID ID user (từ JWT token)
     * @param request Thông tin cập nhật
     * @return BookDetailResponse
     */
    @Transactional
    public BookDetailResponse updateMyPost(Integer postID, Integer userID, UpdatePostRequest request) {
        Post post = postRepository.findById(postID)
                .orElseThrow(() -> new ResourceNotFoundException("Bài đăng không tồn tại với ID: " + postID));
        
        // KIỂM TRA QUYỀN SỞ HỮU
        if (!post.getUser().getUserID().equals(userID)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa bài đăng này");
        }
        
        // KHÔNG cho phép sửa nếu bài đã bán
        if (post.getStatus() == Post.PostStatus.SOLD) {
            throw new RuntimeException("Không thể sửa bài đăng đã bán");
        }
        
        Book book = post.getBook();
        String oldImage = book.getImage(); // Lưu ảnh cũ để xóa sau
        
        // Cập nhật thông tin Book
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getBookCondition() != null) {
            book.setBookCondition(request.getBookCondition());
        }
        if (request.getPrice() != null) {
            book.setPrice(request.getPrice());
        }
        if (request.getContactInfo() != null) {
            book.setContactInfo(request.getContactInfo());
        }
        if (request.getProvince() != null) {
            book.setProvince(request.getProvince());
        }
        if (request.getDistrict() != null) {
            book.setDistrict(request.getDistrict());
        }
        
        // Cập nhật ảnh nếu có ảnh mới
        if (request.getImage() != null && !request.getImage().isEmpty() 
                && !request.getImage().equals(oldImage)) {
            
            // Xóa ảnh cũ nếu có
            if (oldImage != null && !oldImage.isEmpty()) {
                try {
                    boolean deleted = fileStorageService.deleteFile(oldImage);
                    if (deleted) {
                        System.out.println("✅ Đã xóa ảnh cũ: " + oldImage);
                    }
                } catch (Exception e) {
                    // Log lỗi nhưng không throw exception
                    System.err.println("⚠️ Không thể xóa ảnh cũ: " + e.getMessage());
                }
            }
            
            // Cập nhật ảnh mới
            book.setImage(request.getImage());
        }
        
        // Cập nhật mô tả
        if (request.getPostDescription() != null) {
            post.setDescription(request.getPostDescription());
            book.setDescription(request.getPostDescription());
        }
        
        // Reset trạng thái về PENDING nếu bài bị DECLINED
        // Cho phép user sửa và gửi lại để Admin duyệt
        if (post.getStatus() == Post.PostStatus.DECLINED) {
            post.setStatus(Post.PostStatus.PENDING);
        }
        
        // Lưu thay đổi
        bookRepository.save(book);
        Post updatedPost = postRepository.save(post);
        
        return convertToDetailResponse(updatedPost, true);
    }
    
    /**
     * Xóa bài đăng
     * Chỉ User sở hữu mới được xóa
     * Tự động xóa ảnh khi xóa bài
     * 
     * @param postID ID bài đăng
     * @param userID ID user (từ JWT token)
     */
    @Transactional
    public void deleteMyPost(Integer postID, Integer userID) {
        Post post = postRepository.findById(postID)
                .orElseThrow(() -> new ResourceNotFoundException("Bài đăng không tồn tại với ID: " + postID));
        
        // KIỂM TRA QUYỀN SỞ HỮU
        if (!post.getUser().getUserID().equals(userID)) {
            throw new RuntimeException("Bạn không có quyền xóa bài đăng này");
        }
        
        // BẢO VỆ BÀI ĐÃ DUYỆT
        // Không cho phép user tự xóa bài đã APPROVED
        // Phải liên hệ Admin để xóa
        if (post.getStatus() == Post.PostStatus.APPROVED) {
            throw new RuntimeException(
                "Không thể xóa bài đăng đã được duyệt. " +
                "Vui lòng liên hệ Admin hoặc đánh dấu bài đã bán."
            );
        }
        
        // Lấy URL ảnh trước khi xóa bài đăng
        String imageUrl = post.getBook().getImage();
        
        // Xóa bài đăng (Book sẽ tự động xóa do ON DELETE CASCADE)
        postRepository.delete(post);
        
        // Xóa ảnh sau khi xóa bài đăng thành công
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                boolean deleted = fileStorageService.deleteFile(imageUrl);
                if (deleted) {
                    System.out.println("✅ Đã xóa ảnh: " + imageUrl);
                } else {
                    System.out.println("⚠️ Ảnh không tồn tại hoặc đã bị xóa: " + imageUrl);
                }
            } catch (Exception e) {
                // Log lỗi nhưng không throw exception
                // Vì bài đăng đã xóa thành công
                System.err.println("⚠️ Không thể xóa ảnh: " + e.getMessage());
            }
        }
    }
    
    /**
     * Đánh dấu bài đăng đã bán
     * Chỉ User sở hữu mới được đánh dấu
     * 
     * @param postID ID bài đăng
     * @param userID ID user (từ JWT token)
     */
    @Transactional
    public void markAsSold(Integer postID, Integer userID) {
        Post post = postRepository.findById(postID)
                .orElseThrow(() -> new ResourceNotFoundException("Bài đăng không tồn tại với ID: " + postID));
        
        // KIỂM TRA QUYỀN SỞ HỮU
        if (!post.getUser().getUserID().equals(userID)) {
            throw new RuntimeException("Bạn không có quyền cập nhật bài đăng này");
        }
        
        // Chỉ cho phép đánh dấu SOLD nếu bài đã APPROVED
        if (post.getStatus() != Post.PostStatus.APPROVED) {
            throw new RuntimeException("Chỉ có thể đánh dấu đã bán cho bài đăng đã được duyệt");
        }
        
        // Cập nhật trạng thái
        post.setStatus(Post.PostStatus.SOLD);
        postRepository.save(post);
        
        // Note: KHÔNG xóa ảnh khi đánh dấu SOLD
        // Vì có thể cần giữ lại để tham khảo
    }
    
    // ========================================================================
    // PRIVATE HELPER METHODS - Convert Entity sang DTO
    // ========================================================================
    
    /**
     * Convert Post entity sang PostResponse
     * Dùng cho danh sách bài đăng (My Posts, Admin)
     */
    private PostResponse convertToResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setPostID(post.getPostID());
        response.setPostStatus(post.getStatus().name());
        response.setCreatedAt(post.getCreatedAt());


        if (post.getBook() != null) {
            response.setBookID(post.getBook().getBookID());
            response.setTitle(post.getBook().getTitle());
            response.setAuthor(post.getBook().getAuthor());
            response.setPrice(post.getBook().getPrice());
            response.setImage(post.getBook().getImage());
            response.setProvince(post.getBook().getProvince());
            response.setDistrict(post.getBook().getDistrict());
        }
        
        return response;
    }
    
    /**
     * Convert Post entity sang BookDetailResponse
     * Dùng cho chi tiết bài đăng
     * 
     * @param post Post entity
     * @param isAuthenticated User đã đăng nhập hay chưa
     * @return BookDetailResponse
     */
    private BookDetailResponse convertToDetailResponse(Post post, boolean isAuthenticated) {
        BookDetailResponse response = new BookDetailResponse();
        Book book = post.getBook();
        
        // ========== BOOK INFORMATION ==========
        response.setBookID(book.getBookID());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setBookCondition(book.getBookCondition());
        response.setPrice(book.getPrice());
        response.setDescription(book.getDescription());
        response.setImage(book.getImage());
        response.setProvince(book.getProvince());
        response.setDistrict(book.getDistrict());
        response.setCreatedAt(book.getCreatedAt());
        
        // ========== CONTACT INFO (ẨN NẾU CHƯA LOGIN) ==========
        if (isAuthenticated) {
            response.setContactInfo(book.getContactInfo());
        } else {
            response.setContactInfo("🔒 Vui lòng đăng nhập để xem thông tin liên hệ");
        }
        
        // ========== POST INFORMATION ==========
        response.setPostID(post.getPostID());
        response.setPostDescription(post.getDescription());
        response.setPostStatus(post.getStatus().name());
        
        // ========== USER INFORMATION (ẨN NẾU CHƯA LOGIN) ==========
        if (isAuthenticated && post.getUser() != null) {
            response.setUserID(post.getUser().getUserID());
            response.setUserName(post.getUser().getName());
        } else {
            response.setUserID(null);
            response.setUserName("🔒 Đăng nhập để xem");
        }
        
        // ========== CATEGORY INFORMATION ==========
        if (!book.getBookCategories().isEmpty()) {
            BookCategory bookCategory = book.getBookCategories().get(0);
            response.setCategoryID(bookCategory.getCategory().getCategoryID());
            response.setCategoryName(bookCategory.getCategory().getCategoryName());
        }
        
        return response;
    }
}