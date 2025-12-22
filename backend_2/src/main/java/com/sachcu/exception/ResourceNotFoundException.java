package com.sachcu.exception;

/**
 * Exception: ResourceNotFoundException
 * Mô tả: Exception khi không tìm thấy resource
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}