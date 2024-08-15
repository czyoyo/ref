package com.example.ref.repository;

import com.example.ref.entity.NoticeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeCategoryRepository extends JpaRepository<NoticeCategory, Long>, NoticeCategoryCustomRepository {

    boolean existsByTitle(String title);

}
