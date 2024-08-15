package com.example.ref.repository;

import com.example.ref.entity.Admin;
import com.example.ref.entity.AdminProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminProfileImageRepository extends JpaRepository<AdminProfileImage, Long> {

    AdminProfileImage findByAdmin(Admin admin);

    void deleteByAdmin(Admin admin);

}
