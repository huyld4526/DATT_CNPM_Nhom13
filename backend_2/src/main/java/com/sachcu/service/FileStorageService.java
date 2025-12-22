package com.sachcu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service: FileStorageService
 * Mô tả: Xử lý lưu trữ, đọc và xóa file ảnh trên server
 * 
 * Chức năng:
 * - Upload ảnh lên server
 * - Lấy ảnh từ server
 * - Xóa ảnh khỏi server
 * - Validate định dạng và kích thước ảnh
 */
@Service
public class FileStorageService {
    
    private final Path fileStorageLocation;
    private final List<String> allowedExtensions;
    
    public FileStorageService(
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${file.allowed-extensions}") String allowedExtensionsStr) {
        
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.allowedExtensions = Arrays.asList(allowedExtensionsStr.split(","));
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file!", ex);
        }
    }
    
    /**
     * Upload ảnh lên server
     * @param file File ảnh từ client
     * @return Tên file đã lưu (UUID + extension)
     */
    public String storeFile(MultipartFile file) {
        // Validate file
        validateFile(file);
        
        // Lấy tên file gốc và extension
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);
        
        // Tạo tên file mới với UUID để tránh trùng lặp
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
        
        try {
            // Kiểm tra tên file hợp lệ
            if(originalFileName.contains("..")) {
                throw new RuntimeException("Tên file không hợp lệ: " + originalFileName);
            }
            
            // Copy file vào thư mục lưu trữ
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + newFileName + ". Vui lòng thử lại!", ex);
        }
    }
    
    /**
     * Lấy ảnh từ server
     * @param fileName Tên file cần lấy
     * @return Resource chứa file
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File không tồn tại: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File không tồn tại: " + fileName, ex);
        }
    }
    
    /**
     * Xóa ảnh khỏi server
     * @param fileName Tên file cần xóa
     * @return true nếu xóa thành công
     */
    public boolean deleteFile(String fileName) {
        try {
            if (fileName == null || fileName.isEmpty()) {
                return false;
            }
            
            // Chỉ lấy tên file, bỏ qua URL đầy đủ nếu có
            String actualFileName = extractFileNameFromUrl(fileName);
            
            Path filePath = this.fileStorageLocation.resolve(actualFileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Không thể xóa file: " + fileName, ex);
        }
    }
    
    /**
     * Validate file upload
     */
    private void validateFile(MultipartFile file) {
        // Kiểm tra file có rỗng không
        if (file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }
        
        // Kiểm tra kích thước file (10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new RuntimeException("Kích thước file vượt quá 10MB");
        }
        
        // Kiểm tra định dạng file
        String fileName = file.getOriginalFilename();
        String extension = getFileExtension(fileName);
        
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new RuntimeException(
                "Định dạng file không được hỗ trợ. Chỉ chấp nhận: " + String.join(", ", allowedExtensions)
            );
        }
        
        // Kiểm tra Content-Type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("File phải là ảnh");
        }
    }
    
    /**
     * Lấy extension của file
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
    
    /**
     * Trích xuất tên file từ URL
     * VD: http://localhost:8080/api/images/abc-123.jpg -> abc-123.jpg
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null) {
            return null;
        }
        
        // Nếu là URL đầy đủ, lấy phần sau cùng
        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
            String[] parts = fileUrl.split("/");
            return parts[parts.length - 1];
        }
        
        // Nếu là đường dẫn tương đối
        if (fileUrl.contains("/")) {
            String[] parts = fileUrl.split("/");
            return parts[parts.length - 1];
        }
        
        // Nếu chỉ là tên file
        return fileUrl;
    }
}
