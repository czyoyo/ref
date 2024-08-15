package com.example.ref.repository;

import com.example.ref.entity.NoticeCategory;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeCategoryCustomRepository {

    Page<NoticeCategory> findAllByTitleContainingAndCreatedAtBetweenStartDateTimeAndEndDateTime(Pageable pageable, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
