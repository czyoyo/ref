package com.example.ref.repository;

import com.example.ref.entity.Admin;
import com.example.ref.entity.AdminImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminImageRepository extends JpaRepository<AdminImage, Long> {

    AdminImage findByAdminAndType(Admin admin, String type);

    void deleteByAdminAndType(Admin admin, String type);

}
