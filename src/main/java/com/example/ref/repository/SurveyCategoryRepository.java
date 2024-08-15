package com.example.ref.repository;

import com.example.ref.entity.SurveyCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SurveyCategoryRepository extends JpaRepository<SurveyCategory, Long>, SurveyCategoryCustomRepository {

    List<SurveyCategory> findAllByTitle(String title);

}
