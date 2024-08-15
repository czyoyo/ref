package com.example.ref.controller.admin;

import com.example.ref.dto.SurveyDto;
import com.example.ref.request.admin.survey.label.AddSurveyLabelRequestDto;
import com.example.ref.request.admin.survey.label.ModifySurveyLabelRequestDto;
import com.example.ref.request.admin.survey.question.AddSurveyQuestionRequestDto;
import com.example.ref.request.admin.survey.question.ModifySurveyQuestionRequestDto;
import com.example.ref.request.admin.survey.survey.AddSurveyRequestDto;
import com.example.ref.request.admin.survey.survey.GetSurveyListRequestDto;
import com.example.ref.request.admin.survey.survey.ModifySurveyRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/admin/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping(path = "")
    @Operation(summary = "설문 등록", description = "설문을 등록합니다.")
    public CommonResponse<Object> addSurvey(@RequestBody @Valid AddSurveyRequestDto addSurveyRequestDto) {

        surveyService.addSurvey(addSurveyRequestDto);

        return CommonResponse.builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(null)
            .build();

    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "설문 조회", description = "설문을 조회합니다.")
    public CommonResponse<SurveyDto> getSurvey(@PathVariable("id") Long id) {

        SurveyDto survey = surveyService.getSurvey(id);

        return CommonResponse.<SurveyDto>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(survey)
            .build();
    }

    @GetMapping(path = "")
    @Operation(summary = "설문 목록 조회", description = "설문 목록을 조회합니다.")
    public CommonResponse<Page<SurveyDto>> getSurveyList(@ModelAttribute GetSurveyListRequestDto getSurveyListRequestDto, Pageable pageable) {

        // TODO: 응답자수 추가 필요

        Page<SurveyDto> surveyList = surveyService.getSurveyList(pageable, getSurveyListRequestDto);

        return CommonResponse.<Page<SurveyDto>>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(surveyList)
            .build();
    }

    @PostMapping(path = "/questions")
    @Operation(summary = "설문 질문 등록", description = "설문 질문을 등록합니다.")
    public CommonResponse<Object> addSurveyQuestion(@RequestBody @Valid AddSurveyQuestionRequestDto addSurveyQuestionRequestDto) {
        surveyService.addSurveyQuestion(addSurveyQuestionRequestDto);
        return CommonResponse.builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(null)
            .build();
    }

    @PostMapping(path = "/labels")
    @Operation(summary = "설문 라벨 등록", description = "설문 라벨을 등록합니다.")
    public CommonResponse<Object> addSurveyLabel(@RequestBody @Valid AddSurveyLabelRequestDto addSurveyLabelRequestDto) {
        surveyService.addSurveyLabel(addSurveyLabelRequestDto);
        return CommonResponse.builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(null)
            .build();
    }

    @PatchMapping(path = "")
    @Operation(summary = "설문 수정", description = "설문을 수정합니다.")
    public CommonResponse<Object> modifySurvey(@RequestBody @Valid ModifySurveyRequestDto modifySurveyRequestDto) {
        surveyService.modifySurvey(modifySurveyRequestDto);
        return CommonResponse.builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(null)
            .build();
    }

    @PatchMapping(path = "/questions")
    @Operation(summary = "설문 질문 수정", description = "설문 질문을 수정합니다.")
    public CommonResponse<Object> modifySurveyQuestion(@RequestBody @Valid ModifySurveyQuestionRequestDto modifySurveyRequest) {
        surveyService.modifySurveyQuestion(modifySurveyRequest);
        return CommonResponse.builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(null)
            .build();
    }

    @PatchMapping(path = "/labels")
    @Operation(summary = "설문 라벨 수정", description = "설문 라벨을 수정합니다.")
    public CommonResponse<Object> modifySurveyLabel(@RequestBody @Valid ModifySurveyLabelRequestDto modifySurveyLabelRequestDto) {
        surveyService.modifySurveyLabel(modifySurveyLabelRequestDto);
        return CommonResponse.builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(null)
            .build();
    }


    @DeleteMapping(path = "/{id}")
    @Operation(summary = "설문 삭제", description = "설문을 삭제합니다.")
    public CommonResponse<Object> deleteSurvey(@PathVariable("id") Long id) {
        surveyService.deleteSurvey(id);
        return CommonResponse.builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(null)
            .build();
    }

    @DeleteMapping(path = "/questions/{id}")
    @Operation(summary = "설문 질문 삭제", description = "설문 질문을 삭제합니다.")
    public CommonResponse<Object> deleteSurveyQuestion(@PathVariable("id") Long id) {
        surveyService.deleteSurveyQuestion(id);
        return CommonResponse.builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(null)
            .build();
    }

    @DeleteMapping(path = "/labels/{id}")
    @Operation(summary = "설문 라벨 삭제", description = "설문 라벨을 삭제합니다.")
    public CommonResponse<Object> deleteSurveyLabel(@PathVariable("id") Long id) {
        surveyService.deleteSurveyLabel(id);
        return CommonResponse.builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(null)
            .build();
    }


}
