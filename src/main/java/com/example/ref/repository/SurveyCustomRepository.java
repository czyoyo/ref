package com.example.ref.repository;

import com.example.ref.entity.Survey;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SurveyCustomRepository {

    Page<Survey> findAllByContaining(Pageable pageable, String keyword, LocalDateTime startDate, LocalDateTime endDate, String status);

}
