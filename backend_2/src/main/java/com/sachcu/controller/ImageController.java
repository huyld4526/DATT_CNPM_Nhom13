package com.sachcu.controller;

import com.sachcu.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller: ImageController
 * Mô tả: Xử lý các API liên quan đến ảnh
 * 
 * APIs:
 * - POST /images/upload - Upload ảnh (Cần đăng nhập)
 * - GET /images/{fileName} - Xem ảnh (Public)
 * - DELETE /images/{fileName} - Xóa ảnh (Cần đăng nhập)
 */
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageController {
    
    private final FileStorageService fileStorageService;
    
    /**
     * API: Upload ảnh lên server
     * Method: POST
     * Endpoint: /images/upload
     * Auth: CẦN đăng nhập (ROLE_USER)
     * Content-Type: multipart/form-data
     * 
     * Chức năng:
     * - User upload ảnh khi tạo/sửa bài đăng
     * - Trả về URL đầy đủ của ảnh
     * - File được lưu với tên UUID để tránh trùng
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Lưu file và lấy tên file mới
            String fileName = fileStorageService.storeFile(file);
            
            // Tạo URL đầy đủ để truy cập ảnh
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/")
                    .path(fileName)
                    .toUriString();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fileName", fileName);
            response.put("fileUrl", fileDownloadUri);
            response.put("fileSize", file.getSize());
            response.put("fileType", file.getContentType());
            response.put("message", "Upload ảnh thành công");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Upload ảnh thất bại");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * API: Xem/tải ảnh từ server
     * Method: GET
     * Endpoint: /images/{fileName}
     * Auth: KHÔNG CẦN (Public)
     * 
     * Chức năng:
     * - Hiển thị ảnh trong trình duyệt
     * - Sử dụng trong thẻ <img src="..." />
     */
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String fileName,
            HttpServletRequest request) {
        try {
            // Load file as Resource
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            
            // Xác định Content-Type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                // Không thể xác định content type
            }
            
            // Mặc định là octet-stream nếu không xác định được
            if(contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * API: Xóa ảnh khỏi server
     * Method: DELETE
     * Endpoint: /images/{fileName}
     * Auth: CẦN đăng nhập (ROLE_USER hoặc ROLE_ADMIN)
     * 
     * Chức năng:
     * - Xóa ảnh khi user xóa bài đăng
     * - Admin có thể xóa bất kỳ ảnh nào
     * 
     * Note: API này chủ yếu được gọi tự động khi xóa bài đăng
     */
    @DeleteMapping("/{fileName:.+}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> deleteImage(@PathVariable String fileName) {
        try {
            boolean deleted = fileStorageService.deleteFile(fileName);
            
            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("fileName", fileName);
                response.put("message", "Xóa ảnh thành công");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Không tìm thấy ảnh hoặc ảnh đã bị xóa");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * API: Upload nhiều ảnh cùng lúc
     * Method: POST
     * Endpoint: /images/upload-multiple
     * Auth: CẦN đăng nhập (ROLE_USER)
     * Content-Type: multipart/form-data
     * 
     * Chức năng: Upload tối đa 5 ảnh cùng lúc
     */
    @PostMapping("/upload-multiple")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files) {
        try {
            if (files.length > 5) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Chỉ được upload tối đa 5 ảnh cùng lúc"
                ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("uploadedFiles", new java.util.ArrayList<>());
            
            for (MultipartFile file : files) {
                try {
                    String fileName = fileStorageService.storeFile(file);
                    String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/images/")
                            .path(fileName)
                            .toUriString();
                    
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("fileName", fileName);
                    fileInfo.put("fileUrl", fileUrl);
                    fileInfo.put("fileSize", file.getSize());
                    
                    ((java.util.ArrayList) response.get("uploadedFiles")).add(fileInfo);
                } catch (Exception e) {
                    // Skip file lỗi và tiếp tục
                    Map<String, Object> fileError = new HashMap<>();
                    fileError.put("originalName", file.getOriginalFilename());
                    fileError.put("error", e.getMessage());
                    
                    if (!response.containsKey("errors")) {
                        response.put("errors", new java.util.ArrayList<>());
                    }
                    ((java.util.ArrayList) response.get("errors")).add(fileError);
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}