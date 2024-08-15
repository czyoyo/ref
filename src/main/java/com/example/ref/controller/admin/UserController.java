package com.example.ref.controller.admin;

import com.example.ref.dto.UserDto;
import com.example.ref.request.admin.user.AddUserRequestDto;
import com.example.ref.request.admin.user.GetUserRequestDto;
import com.example.ref.request.admin.user.ModifyUserRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/admin/users")
public class UserController {

    private final UserService userService;

    @PostMapping(path = "/reset/pw/{id}")
    @Operation(summary = "비밀번호 초기화", description = "비밀번호를 초기화합니다.")
    public CommonResponse<Object> resetPassword(@PathVariable("id") Long id) {
        userService.resetPassword(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();
    }


    @PostMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "유저 등록", description = "유저를 등록합니다.")
    public CommonResponse<Object> addUser(@ModelAttribute @Valid AddUserRequestDto addUserRequestDto) {
        userService.addUser(addUserRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();
    }

    // 아이디 중복 확인
    @GetMapping(path = "/check_user_id/{userId}")
    @Operation(summary = "유저 아이디 중복 확인", description = "유저 아이디 중복을 확인합니다.")
    public CommonResponse<Object> checkUserId(@PathVariable("userId") String userId) {
        boolean isDuplicate = userService.checkUserId(userId);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(isDuplicate)
            .build();
    }

    // 닉네임 중복 확인
    @GetMapping(path = "/check_nickname/{nickname}")
    @Operation(summary = "유저 닉네임 중복 확인", description = "유저 닉네임 중복을 확인합니다.")
    public CommonResponse<Object> checkUserNickname(@PathVariable("nickname") String nickname) {
        boolean isDuplicate = userService.checkUserNickname(nickname);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(isDuplicate)
            .build();
    }

    @GetMapping(path = "")
    @Operation(summary = "유저 목록 조회", description = "유저 목록을 조회합니다.")
    public CommonResponse<Page<UserDto>> getUserList(@ModelAttribute GetUserRequestDto getUserRequestDto, Pageable pageable) {
        Page<UserDto> userList = userService.getUserList(pageable, getUserRequestDto);

        return CommonResponse.<Page<UserDto>>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(userList)
            .build();
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "유저 조회", description = "유저를 조회합니다.")
    public CommonResponse<Object> getUser(@PathVariable("id") Long id) {
        UserDto user = userService.getUser(id);

        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(user)
            .build();
    }

    @PatchMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "유저 수정", description = "유저를 수정합니다.")
    public CommonResponse<Object> modifyUser(@ModelAttribute @Valid ModifyUserRequestDto modifyUserRequestDto)
        throws JsonProcessingException {
        userService.modifyUser(modifyUserRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }

    // 유저 프로필 이미지 비동기 삭제
    @DeleteMapping(path = "/profile-image/{id}")
    @Operation(summary = "유저 프로필 이미지 삭제", description = "유저 프로필 이미지를 삭제합니다.")
    public CommonResponse<Object> deleteProfileImage(@PathVariable("id") Long id) {
        userService.deleteProfileImage(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();
    }


}
