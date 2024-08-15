package com.example.ref.service.admin;

import com.example.ref.dto.SurveyDto;
import com.example.ref.entity.Admin;
import com.example.ref.entity.Survey;
import com.example.ref.entity.SurveyCategory;
import com.example.ref.entity.SurveyLabel;
import com.example.ref.entity.SurveyQuestion;
import com.example.ref.repository.AdminRepository;
import com.example.ref.repository.SurveyCategoryRepository;
import com.example.ref.repository.SurveyLabelRepository;
import com.example.ref.repository.SurveyQuestionRepository;
import com.example.ref.repository.SurveyRepository;
import com.example.ref.request.admin.survey.label.AddSurveyLabelRequestDto;
import com.example.ref.request.admin.survey.label.ModifySurveyLabelRequestDto;
import com.example.ref.request.admin.survey.question.AddSurveyQuestionRequestDto;
import com.example.ref.request.admin.survey.question.ModifySurveyQuestionRequestDto;
import com.example.ref.request.admin.survey.survey.AddSurveyRequestDto;
import com.example.ref.request.admin.survey.survey.AddSurveyRequestDto.SurveyLabelRequest;
import com.example.ref.request.admin.survey.survey.AddSurveyRequestDto.SurveyQuestionRequest;
import com.example.ref.request.admin.survey.survey.GetSurveyListRequestDto;
import com.example.ref.request.admin.survey.survey.ModifySurveyRequestDto;
import com.example.ref.rules.SurveyType;
import com.example.ref.util.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyCategoryRepository surveyCategoryRepository;
    private final SurveyLabelRepository surveyLabelRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final AdminRepository adminRepository;

    public void addSurvey(AddSurveyRequestDto addSurveyRequestDto) {

        String userId = AuthUtils.getUserId();
        Admin admin = adminRepository.findByAdminId(userId);
        if(admin == null){
            throw new IllegalArgumentException("존재하지 않는 어드민입니다.");
        }

        SurveyCategory surveyCategory = surveyCategoryRepository.findById(addSurveyRequestDto.getSurveyCategoryIdx()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 종료일이 시작일보다 빠르면 에러
        if (addSurveyRequestDto.getEndDateTime().isBefore(addSurveyRequestDto.getStartDateTime())) {
            throw new IllegalArgumentException("종료일이 시작일보다 빠릅니다.");
        }

        // 일단 Survey Entity 생성
        Survey survey = Survey.builder()
            .surveyCategory(surveyCategory)
            .title(addSurveyRequestDto.getTitle())
            .startDateTime(addSurveyRequestDto.getStartDateTime())
            .endDateTime(addSurveyRequestDto.getEndDateTime())
            .content(addSurveyRequestDto.getContent())
            .writer(admin)
            .build();
        surveyRepository.save(survey);

        // Survey Parts 생성 (여러개)
        if(!addSurveyRequestDto.getSurveyQuestionRequestList().isEmpty()){
            for(SurveyQuestionRequest surveyQuestionRequest : addSurveyRequestDto.getSurveyQuestionRequestList()){

                // Type 체크
                if(surveyQuestionRequest.getType() == null){
                    throw new IllegalArgumentException("설문 문항 타입을 선택해주세요.");
                }

                // Type SurveyType 체크 SurveyType.values() 에 없으면 에러
                boolean isSurveyType = false;
                for(SurveyType surveyType : SurveyType.values()){
                    if (surveyType.getType().equals(surveyQuestionRequest.getType())) {
                        isSurveyType = true;
                        break;
                    }
                }
                if(!isSurveyType){
                    throw new IllegalArgumentException("설문 문항 타입을 잘못 선택하셨습니다.");
                }

                SurveyQuestion surveyQuestion = SurveyQuestion.builder()
                    .survey(survey)
                    .title(surveyQuestionRequest.getTitle())
                    .type(surveyQuestionRequest.getType())  // SurveyType check, radio, text 에 있는 타입만 들어옴
                    .build();

                surveyQuestionRepository.save(surveyQuestion);

                for(SurveyLabelRequest surveyLabelRequest : surveyQuestionRequest.getSurveyLabelRequestList()){
                    SurveyLabel surveyLabel = SurveyLabel.builder()
                        .title(surveyLabelRequest.getTitle())
                        .isCheck(surveyLabelRequest.isCheck())
                        .surveyQuestion(surveyQuestion)
                        .build();
                    surveyLabelRepository.save(surveyLabel);
                }

            }
        }
    }

    public SurveyDto getSurvey(Long id) {

        Survey withExtraById = surveyRepository.findWithExtraById(id);
        if(withExtraById == null){
            throw new IllegalArgumentException("존재하지 않는 설문입니다.");
        }
        return SurveyDto.convertToDto(withExtraById);
    }

    public Page<SurveyDto> getSurveyList(Pageable pageable, GetSurveyListRequestDto getSurveyListRequestDto) {

        Page<Survey> surveyList = surveyRepository.findAllByContaining(
            pageable,
            getSurveyListRequestDto.getKeyword(),
            getSurveyListRequestDto.getStartDate() != null ? getSurveyListRequestDto.getStartDate().atStartOfDay() : null,
            getSurveyListRequestDto.getEndDate() != null ? getSurveyListRequestDto.getEndDate().atTime(23, 59, 59) : null,
            getSurveyListRequestDto.getSearchStatus()
        );
        return surveyList.map(SurveyDto::convertToDto);
    }

    public void deleteSurvey(Long id) {

        Survey survey = surveyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문입니다."));

        surveyRepository.delete(survey);
    }

    public void addSurveyQuestion(AddSurveyQuestionRequestDto addSurveyQuestionRequestDto) {

        Survey survey = surveyRepository.findById(addSurveyQuestionRequestDto.getSurveyIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문입니다."));

        boolean isSurveyType = false;
        for(SurveyType surveyType : SurveyType.values()){
            if (surveyType.getType().equals(addSurveyQuestionRequestDto.getType())) {
                isSurveyType = true;
                break;
            }
        }
        if(!isSurveyType){
            throw new IllegalArgumentException("설문 문항 타입을 잘못 선택하셨습니다.");
        }

        SurveyQuestion surveyQuestion = SurveyQuestion.builder()
            .title(addSurveyQuestionRequestDto.getTitle())
            .type(addSurveyQuestionRequestDto.getType())
            .build();

        surveyQuestion.setSurvey(survey);
        surveyQuestionRepository.save(surveyQuestion);
    }

    public void addSurveyLabel(AddSurveyLabelRequestDto addSurveyLabelRequestDto) {

        SurveyQuestion surveyQuestion = surveyQuestionRepository.findById(addSurveyLabelRequestDto.getSurveyQuestionIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문 문항입니다."));

        SurveyLabel surveyLabel = SurveyLabel.builder()
            .title(addSurveyLabelRequestDto.getTitle())
            .isCheck(addSurveyLabelRequestDto.isCheck())
            .build();

        surveyLabel.setSurveyQuestion(surveyQuestion);
        surveyLabelRepository.save(surveyLabel);
    }


    public void deleteSurveyQuestion(Long id) {

        SurveyQuestion surveyQuestion = surveyQuestionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문 문항입니다."));

        surveyQuestionRepository.delete(surveyQuestion);
    }

    public void modifySurveyQuestion(ModifySurveyQuestionRequestDto modifySurveyQuestionRequestDto) {

        SurveyQuestion surveyQuestion = surveyQuestionRepository.findById(
                modifySurveyQuestionRequestDto.getSurveyQuestionIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문 문항입니다."));
        surveyQuestion.modifySurveyQuestion(modifySurveyQuestionRequestDto);
    }

    public void deleteSurveyLabel(Long id) {

        SurveyLabel surveyLabel = surveyLabelRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문 문항 라벨입니다."));

        surveyLabelRepository.delete(surveyLabel);
    }

    public void modifySurveyLabel(ModifySurveyLabelRequestDto modifySurveyLabelRequestDto) {

        SurveyLabel surveyLabel = surveyLabelRepository.findById(modifySurveyLabelRequestDto.getSurveyLabelIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문 문항 라벨입니다."));
        surveyLabel.modifySurveyLabel(modifySurveyLabelRequestDto);
    }


    public void modifySurvey(ModifySurveyRequestDto modifySurveyRequestDto) {

        Survey survey = surveyRepository.findById(modifySurveyRequestDto.getSurveyIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문입니다."));

        SurveyCategory surveyCategory = surveyCategoryRepository.findById(modifySurveyRequestDto.getSurveyCategoryIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 종료일이 시작일보다 빠르면 에러
        if (modifySurveyRequestDto.getEndDateTime().isBefore(modifySurveyRequestDto.getStartDateTime())) {
            throw new IllegalArgumentException("종료일이 시작일보다 빠릅니다.");
        }

        survey.modifySurvey(Survey.builder()
            .title(modifySurveyRequestDto.getTitle())
            .startDateTime(modifySurveyRequestDto.getStartDateTime())
            .endDateTime(modifySurveyRequestDto.getEndDateTime())
            .content(modifySurveyRequestDto.getContent())
            .build());
        survey.setSurveyCategory(surveyCategory);

    }




}
