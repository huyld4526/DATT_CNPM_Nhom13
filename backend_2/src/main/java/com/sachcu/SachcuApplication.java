package com.sachcu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Main Application Class
 * Mô tả: Entry point của ứng dụng Spring Boot
 */
@SpringBootApplication
public class SachcuApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SachcuApplication.class, args);
        System.out.println("==============================================");
        System.out.println("🚀 Sách Cũ Backend API is running!");
        System.out.println("📍 API Base URL: http://localhost:8080/api");
        System.out.println("==============================================");
          System.out.println(
            new BCryptPasswordEncoder().encode("123456")
        );
    }
}