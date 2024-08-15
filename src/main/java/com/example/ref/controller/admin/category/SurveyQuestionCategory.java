package com.example.ref.controller.admin.category;

import com.example.ref.service.admin.SurveyQuestionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/admin/categories/survey/question")
public class SurveyQuestionCategory {

    private final SurveyQuestionCategoryService surveyQuestionCategoryService;





}
