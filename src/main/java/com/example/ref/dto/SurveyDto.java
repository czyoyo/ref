package com.example.ref.dto;

import com.example.ref.entity.Admin;
import com.example.ref.entity.Survey;
import com.example.ref.entity.SurveyCategory;
import com.example.ref.entity.SurveyQuestion;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyDto {

    @Schema(description = "설문 인덱스", example = "1")
    private Long id;

    @Schema(description = "설문 카테고리", example = "설문 카테고리")
    private SurveyCategoryDto surveyCategory;

    @Schema(description = "설문명", example = "설문명")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "시작일", example = "2021-01-01 00:00:00")
    private LocalDateTime startDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "종료일", example = "2021-01-01 00:00:00")
    private LocalDateTime endDateTime;

    @Schema(description = "설문 내용", example = "설문 내용")
    private String content;

    @Schema(description = "설문 문항 리스트")
    private List<SurveyQuestionDto> surveyQuestionList;

    // 진행상태
    @Schema(description = "진행상태", example = "대기중 / 완료")
    private String status;

    @Schema(description = "작성자 정보")
    private AdminDto writer;

    @Schema(description = "등록 일자")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "응답수")
    @Builder.Default
    private Integer responseCount = 0;


    // 시작일과, 종료일로 진행상태를 표시
    private static String status(LocalDateTime startDateTime, LocalDateTime endDateTime){
        LocalDateTime now = LocalDateTime.now();
        if(now.isBefore(startDateTime)){
            return "대기중";
        } else if(now.isAfter(endDateTime)){
            return "완료";
        } else if(now.isAfter(startDateTime) && now.isBefore(endDateTime)){
            return "진행중";
        } else {
            return "알수없음";
        }
    }

    private static SurveyDto.SurveyDtoBuilder surveyDtoBuilder(Survey survey){
        return SurveyDto.builder()
            .id(survey.getId())
            .title(survey.getTitle())
            .startDateTime(survey.getStartDateTime())
            .endDateTime(survey.getEndDateTime())
            .status(status(survey.getStartDateTime(), survey.getEndDateTime()))
            .createdAt(survey.getCreatedAt())
            .content(survey.getContent());
    }

    private static SurveyCategoryDto surveyCategoryDto(Survey survey){
        if(survey.getSurveyCategory() == null){
            return null;
        }
        SurveyCategory surveyCategory = survey.getSurveyCategory();
        return SurveyCategoryDto.builder()
            .id(surveyCategory.getId())
            .title(surveyCategory.getTitle())
            .classification(surveyCategory.getClassification())
            .memo(surveyCategory.getMemo())
            .build();
    }

    private static List<SurveyQuestionDto> surveyQuestionDtoList(Survey survey){
        return Optional.ofNullable(survey.getSurveyQuestionList()).map(
            surveyQuestionList -> surveyQuestionList.stream()
                .map(surveyQuestion -> SurveyQuestionDto.builder()
                    .id(surveyQuestion.getId())
                    .title(surveyQuestion.getTitle())
                    .type(surveyQuestion.getType())
                    .surveyLabelList(surveyLabelDtoList(surveyQuestion))
                    .build())
                .toList()
        ).orElse(Collections.emptyList());
    }

    private static AdminDto writerDto(Survey survey){
        if(survey.getWriter() == null){
            return null;
        }
        Admin admin = survey.getWriter();
        return Optional.of(admin).map(
            deepAdmin -> AdminDto.builder()
                .id(deepAdmin.getId())
                .adminId(deepAdmin.getAdminId())
                .nickname(deepAdmin.getNickname())
                .build()
        ).orElse(null);
    }

    private static List<SurveyLabelDto> surveyLabelDtoList(SurveyQuestion surveyQuestion){
        return Optional.ofNullable(surveyQuestion.getSurveyLabelList()).map(
            surveyLabelList -> surveyLabelList.stream()
                .map(surveyLabel -> SurveyLabelDto.builder()
                    .id(surveyLabel.getId())
                    .title(surveyLabel.getTitle())
                    .isCheck(surveyLabel.isCheck())
                    .build())
                .toList()
        ).orElse(Collections.emptyList());
    }

    public static SurveyDto convertToDto(Survey survey) {
        SurveyDtoBuilder surveyDtoBuilder = surveyDtoBuilder(survey);
        surveyDtoBuilder.surveyCategory(surveyCategoryDto(survey));
        surveyDtoBuilder.surveyQuestionList(surveyQuestionDtoList(survey));
        surveyDtoBuilder.writer(writerDto(survey));
        return surveyDtoBuilder.build();
    }


}
