package com.example.ref.repository;

import com.example.ref.entity.SurveyCategory;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;

public interface SurveyCategoryCustomRepository {

    Page<SurveyCategory> findAllByClassificationAndTitleContainingAndCreatedAtBetween(
        String keyword, LocalDateTime startDate, LocalDateTime endDate,
        org.springframework.data.domain.Pageable pageable);

}
