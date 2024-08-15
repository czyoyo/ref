package com.example.ref.repository;

import com.example.ref.entity.UserCategory;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCategoryCustomRepository {

    // 커스텀 메소드 하나씩 구현
    Page<UserCategory> findAllByClassificationAndTitleContainingAndCreatedAtBetween(String keyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
