package com.sachcu.repository;

import com.sachcu.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository: AdminRepository
 * Mô tả: Truy vấn dữ liệu bảng Admin
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    
    Optional<Admin> findByEmail(String email);
}