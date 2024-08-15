package com.example.ref.controller.admin.category;

import com.example.ref.dto.NoticeCategoryDto;
import com.example.ref.request.admin.category.notice.AddNoticeCategoryRequestDto;
import com.example.ref.request.admin.category.notice.GetNoticeCategoryListRequestDto;
import com.example.ref.request.admin.category.notice.ModifyNoticeCategoryRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.NoticeCategoryService;
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
@RequestMapping(path = "/api/admin/categories/notice")
public class NoticeCategoryController {

    private final NoticeCategoryService noticeCategoryService;


    @GetMapping(path = "")
    @Operation(summary = "공지사항 카테고리 목록 조회", description = "공지사항 카테고리 목록을 조회합니다.")
    public CommonResponse<Page<NoticeCategoryDto>> getNoticeCategoryList(Pageable pageable, @ModelAttribute GetNoticeCategoryListRequestDto getNoticeCategoryListRequestDto) {
        Page<NoticeCategoryDto> noticeCategoryList = noticeCategoryService.getNoticeCategoryList(pageable,
            getNoticeCategoryListRequestDto);
        return CommonResponse.<Page<NoticeCategoryDto>>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(noticeCategoryList)
            .build();
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "공지사항 카테고리 상세 조회", description = "공지사항 카테고리 상세를 조회합니다.")
    public CommonResponse<NoticeCategoryDto> getNoticeCategory(@PathVariable("id") Long id) {
        NoticeCategoryDto noticeCategory = noticeCategoryService.getNoticeCategoryDetail(id);
        return CommonResponse.<NoticeCategoryDto>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(noticeCategory)
            .build();
    }

    @PostMapping(path = "")
    @Operation(summary = "공지사항 카테고리 등록", description = "공지사항 카테고리를 등록합니다.")
    public CommonResponse<Object> addNoticeCategory(@RequestBody @Valid AddNoticeCategoryRequestDto addNoticeCategoryRequestDto) {
        noticeCategoryService.addNoticeCategory(addNoticeCategoryRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }

    @PatchMapping(path = "")
    @Operation(summary = "공지사항 카테고리 수정", description = "공지사항 카테고리를 수정합니다.")
    public CommonResponse<Object> modifyNoticeCategory(@RequestBody @Valid ModifyNoticeCategoryRequestDto modifyNoticeCategoryRequestDto) {
        noticeCategoryService.modifyNoticeCategory(modifyNoticeCategoryRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "공지사항 카테고리 삭제", description = "공지사항 카테고리를 삭제합니다.")
    public CommonResponse<Object> deleteNoticeCategory(@PathVariable("id") Long id) {
        noticeCategoryService.deleteNoticeCategory(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }



}
