package com.example.ref.repository;

import com.example.ref.entity.User;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCustomRepository {

    Page<User> finaAllByNicknameContainingAndCreatedAtBetween(Pageable pageable, String keyword, LocalDateTime startDate, LocalDateTime endDate, Long userCategoryId, Long userCategoryParentId);

}
