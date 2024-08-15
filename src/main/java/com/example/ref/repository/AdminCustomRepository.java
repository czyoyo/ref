package com.example.ref.repository;

import com.example.ref.entity.Admin;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminCustomRepository {
    Page<Admin> finaAllByNicknameContainingAndCreatedAtBetween(Pageable pageable, String keyword, LocalDateTime startDate, LocalDateTime endDate, Long userCategoryId, Long userCategoryParentId);
}
