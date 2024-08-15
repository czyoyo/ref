package com.example.ref.controller.admin.category;

import com.example.ref.dto.SurveyCategoryDto;
import com.example.ref.request.admin.category.survey.AddSurveyCategoryRequestDto;
import com.example.ref.request.admin.category.survey.GetSurveyCategoryRequestDto;
import com.example.ref.request.admin.category.survey.ModifySurveyCategoryRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.SurveyCategoryService;
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
@RequestMapping(path = "/api/admin/categories/survey")
public class SurveyCategoryController {

    private final SurveyCategoryService surveyCategoryService;

    @PostMapping(path = "")
    @Operation(summary = "설문 카테고리 등록", description = "설문 카테고리를 등록합니다.")
    public CommonResponse<Object> addSurveyCategory(@RequestBody @Valid AddSurveyCategoryRequestDto addSurveyCategoryRequestDto) {
        surveyCategoryService.addSurveyCategory(addSurveyCategoryRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "설문 카테고리 조회", description = "설문 카테고리를 조회합니다.")
    public CommonResponse<Object> getSurveyCategory(@PathVariable("id") Long id) {
        SurveyCategoryDto surveyCategory = surveyCategoryService.getSurveyCategory(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(surveyCategory)
            .build();
    }

    @GetMapping(path = "")
    @Operation(summary = "설문 카테고리 목록 조회", description = "설문 카테고리 목록을 조회합니다.")
    public CommonResponse<Object> getSurveyCategoryList(Pageable pageable, @ModelAttribute GetSurveyCategoryRequestDto getSurveyCategoryRequestDto) {
        Page<SurveyCategoryDto> surveyCategoryList = surveyCategoryService.getSurveyCategoryList(
            pageable, getSurveyCategoryRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(surveyCategoryList)
            .build();
    }


    @PatchMapping(path = "")
    @Operation(summary = "설문 카테고리 수정", description = "설문 카테고리를 수정합니다.")
    public CommonResponse<Object> modifySurveyCategory(@RequestBody @Valid ModifySurveyCategoryRequestDto modifySurveyCategoryRequestDto) {
        surveyCategoryService.modifySurveyCategory(modifySurveyCategoryRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "설문 카테고리 삭제", description = "설문 카테고리를 삭제합니다.")
    public CommonResponse<Object> deleteSurveyCategory(@PathVariable("id") Long id) {
        surveyCategoryService.deleteSurveyCategory(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }


}
