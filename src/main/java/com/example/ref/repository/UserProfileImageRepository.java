package com.example.ref.repository;

import com.example.ref.entity.User;
import com.example.ref.entity.UserProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileImageRepository extends JpaRepository<UserProfileImage, Long> {

    UserProfileImage findByUser(User userIdx);

}
