package com.example.ref.service.admin;

import com.example.ref.dto.SurveyCategoryDto;
import com.example.ref.entity.SurveyCategory;
import com.example.ref.repository.AdminRepository;
import com.example.ref.repository.SurveyCategoryRepository;
import com.example.ref.request.admin.category.survey.AddSurveyCategoryRequestDto;
import com.example.ref.request.admin.category.survey.GetSurveyCategoryRequestDto;
import com.example.ref.request.admin.category.survey.ModifySurveyCategoryRequestDto;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyCategoryService {

    private final SurveyCategoryRepository surveyCategoryRepository;
    private final AdminRepository adminRepository;


    public void addSurveyCategory(AddSurveyCategoryRequestDto addSurveyCategoryRequestDto) {

        // 타이틀 중복 체크
        List<SurveyCategory> surveyCategoryList = surveyCategoryRepository.findAllByTitle(
            addSurveyCategoryRequestDto.getTitle());
        if(!surveyCategoryList.isEmpty()) {
            throw new IllegalArgumentException("중복된 타이틀이 존재합니다.");
        }

        SurveyCategory surveyCategory =
            SurveyCategory.builder()
                .title(addSurveyCategoryRequestDto.getTitle())
                .memo(addSurveyCategoryRequestDto.getMemo())
                .classification(addSurveyCategoryRequestDto.getClassification())
                .build();

        surveyCategoryRepository.save(surveyCategory);
    }

    public SurveyCategoryDto getSurveyCategory(Long id) {

        SurveyCategory surveyCategory = surveyCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));

        return SurveyCategoryDto.convertToDto(surveyCategory);
    }

    public Page<SurveyCategoryDto> getSurveyCategoryList(Pageable pageable, GetSurveyCategoryRequestDto getSurveyCategoryRequestDto) {

        Page<SurveyCategory> surveyCategoryList = surveyCategoryRepository.findAllByClassificationAndTitleContainingAndCreatedAtBetween(
            getSurveyCategoryRequestDto.getKeyword(),
            getSurveyCategoryRequestDto.getStartDate() != null ? getSurveyCategoryRequestDto.getStartDate().atStartOfDay() : null,
            getSurveyCategoryRequestDto.getEndDate() != null ? getSurveyCategoryRequestDto.getEndDate().atTime(23, 59, 59) : null,
            pageable
        );

        return surveyCategoryList.map(SurveyCategoryDto::convertToDto);
    }


    public void modifySurveyCategory(ModifySurveyCategoryRequestDto modifySurveyCategoryRequestDto) {

        SurveyCategory surveyCategory = surveyCategoryRepository.findById(
            modifySurveyCategoryRequestDto.getSurveyCategoryIdx()).orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));

        surveyCategory.modifySurveyCategory(modifySurveyCategoryRequestDto);

    }

    public void deleteSurveyCategory(Long id) {

        SurveyCategory surveyCategory = surveyCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));

        if(!surveyCategory.getSurveyList().isEmpty()) {
            throw new IllegalArgumentException("연결된 설문이 존재합니다.");
        }

        surveyCategoryRepository.delete(surveyCategory);
    }






}
