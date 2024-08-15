package com.example.ref.controller.admin.category;

import com.example.ref.dto.UserCategoryDto;
import com.example.ref.request.admin.category.user.AddUserCategoryRequestDto;
import com.example.ref.request.admin.category.user.GetUserCategoryListRequestDto;
import com.example.ref.request.admin.category.user.ModifyUserCategoryRequestDto;
import com.example.ref.request.admin.category.user.TitleCheckRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.UserCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(path = "/api/admin/categories/user")
public class UserCategoryController {

    private final UserCategoryService userCategoryService;

    @PostMapping(path = "")
    @Operation(summary = "사용자 권한 카테고리 등록", description = "사용자 권한 카테고리를 등록합니다.")
    public CommonResponse<Object> addUserCategory(@RequestBody @Valid AddUserCategoryRequestDto addUserCategoryRequestDto) {

        userCategoryService.addUserCategory(addUserCategoryRequestDto);

        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }


    // 1차 카테고리로 2차 카테고리 조회
    @GetMapping(path = "/{id}/sub")
    @Operation(summary = "사용자 권한 카테고리 하위 카테고리 조회", description = "사용자 권한 카테고리의 하위 카테고리를 조회합니다.")
    public CommonResponse<List<UserCategoryDto>> getUserCategorySubList(@PathVariable("id") Long id) {
        List<UserCategoryDto> userCategorySubList = userCategoryService.getUserCategorySubList(id);
        return CommonResponse.<List<UserCategoryDto>>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(userCategorySubList)
            .build();
    }

    @GetMapping(path = "/first")
    @Operation(summary = "사용자 권한 카테고리 1차 카테고리 조회", description = "사용자 권한 카테고리의 1차 카테고리를 조회합니다.")
    public CommonResponse<List<UserCategoryDto>> getFirstUserCategoryList() {
        List<UserCategoryDto> firstUserCategoryList = userCategoryService.getFirstUserCategoryList();
        return CommonResponse.<List<UserCategoryDto>>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(firstUserCategoryList)
            .build();
    }

    // 타이틀 중복 체크
    @GetMapping(path = "/check/title")
    @Operation(summary = "사용자 권한 카테고리 타이틀 중복 체크", description = "사용자 권한 카테고리 타이틀 중복을 체크합니다.")
    public CommonResponse<Object> checkUserCategoryTitle(@ModelAttribute TitleCheckRequestDto titleCheckRequestDto) {
        boolean bool = userCategoryService.checkUserCategoryTitle(titleCheckRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(bool)
            .build();
    }


    // 리스트
    @GetMapping(path = "")
    @Operation(summary = "사용자 권한 카테고리 목록 조회", description = "사용자 권한 카테고리 목록을 조회합니다.")
    public CommonResponse<Object> getUserCategoryList(Pageable pageable, @ModelAttribute GetUserCategoryListRequestDto getUserCategoryListRequestDto) {
        Page<UserCategoryDto> userCategoryList = userCategoryService.getUserCategoryList(pageable,
            getUserCategoryListRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(userCategoryList)
            .build();
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "사용자 권한 카테고리 조회", description = "사용자 권한 카테고리를 조회합니다.")
    public CommonResponse<UserCategoryDto> getUserCategory(@PathVariable("id") Long id) {
        UserCategoryDto userCategory = userCategoryService.getUserCategory(id);
        return CommonResponse.<UserCategoryDto>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(userCategory)
            .build();
    }


    // 수정
    @PatchMapping(path = "")
    @Operation(summary = "사용자 권한 카테고리 수정", description = "사용자 권한 카테고리를 수정합니다.")
    public CommonResponse<Object> modifyUserCategory(@RequestBody @Valid ModifyUserCategoryRequestDto modifyUserCategoryRequestDto) {
        userCategoryService.modifyUserCategory(modifyUserCategoryRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }
    // 삭제
    @DeleteMapping(path = "/{id}")
    @Operation(summary = "사용자 권한 카테고리 삭제", description = "사용자 권한 카테고리를 삭제합니다.")
    public CommonResponse<Object> deleteUserCategory(@PathVariable("id") Long id) {
        userCategoryService.deleteUserCategory(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }





}
