package com.example.ref.controller.admin;

import com.example.ref.dto.AdminDto;
import com.example.ref.request.admin.admin.AddAdminRequestDto;
import com.example.ref.request.admin.admin.ChangeMyPasswordRequestDto;
import com.example.ref.request.admin.admin.GetAdminRequestDto;
import com.example.ref.request.admin.admin.LoginAdminRequestDto;
import com.example.ref.request.admin.admin.ModifyAdminRequestDto;
import com.example.ref.request.admin.admin.ModifyMyInfoRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.response.JwtResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(path = "/api/admin/admins")
@Slf4j
public class AdminController {

    private final AdminService adminService;


    // 어드민 로그인
    @PostMapping(path = "/login")
    @Operation(summary = "로그인", description = "로그인합니다.")
    public ResponseEntity<CommonResponse<JwtResponse>> login(@RequestBody @Valid LoginAdminRequestDto loginAdminRequestDto) {
        return adminService.login(loginAdminRequestDto);
    }

    // 어드민 리스트 (페이징)
    @GetMapping(path = "")
    @Operation(summary = "어드민 목록 조회", description = "어드민 목록을 조회합니다.")
    public CommonResponse<Page<AdminDto>> getAdminList(@ModelAttribute GetAdminRequestDto getAdminRequestDto, Pageable pageable) {
        Page<AdminDto> adminList = adminService.getAdminList(pageable, getAdminRequestDto);

        return CommonResponse.<Page<AdminDto>>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(adminList)
            .build();
    }

    // 단일 어드민 정보 조회
    @GetMapping(path = "/{id}")
    @Operation(summary = "어드민 조회", description = "어드민을 조회합니다.")
    public CommonResponse<AdminDto> getAdmin(@PathVariable("id") Long id) {
        AdminDto admin = adminService.getAdmin(id);

        return CommonResponse.<AdminDto>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(admin)
            .build();
    }

    @GetMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "토큰을 재발급합니다.")
    public ResponseEntity<CommonResponse<JwtResponse>> refresh(HttpServletRequest httpServletRequest) {
        return adminService.refresh(httpServletRequest);
    }


    // 어드민 추가
    @PostMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "어드민 등록", description = "어드민을 등록합니다.")
    public CommonResponse<Object> registerAdmin(@ModelAttribute @Valid AddAdminRequestDto addAdminRequestDto)
        throws IOException {
        return adminService.addAdmin(addAdminRequestDto);
    }

    // 어드민 수정
    @PatchMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "어드민 수정", description = "어드민을 수정합니다.")
    public CommonResponse<Object> modifyAdmin(@ModelAttribute @Valid ModifyAdminRequestDto modifyAdminRequestDto)
        throws IOException {
        adminService.modifyAdmin(modifyAdminRequestDto);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();
    }


    // 어드민 자기 정보 수정
    @PatchMapping(path = "/my-info", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "어드민 자기 정보 수정", description = "어드민 자기 정보를 수정합니다.")
    public CommonResponse<Object> modifyMyInfo(@ModelAttribute @Valid ModifyMyInfoRequestDto request)
        throws IOException {
        adminService.modifyMyInfo(request);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();
    }

    @GetMapping(path = "/my-info")
    @Operation(summary = "어드민 자기 정보 조회", description = "어드민 자기 정보를 조회합니다.")
    public CommonResponse<AdminDto> getMyInfo() {
        AdminDto adminDto = adminService.getMyInfo();

        return CommonResponse.<AdminDto>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(adminDto)
            .build();
    }

    // 마이페이지 패스워드 변경
    @PatchMapping(path = "/my-info/password")
    @Operation(summary = "패스워드 변경", description = "패스워드를 변경합니다.")
    public CommonResponse<Object> changePassword(@RequestBody @Valid ChangeMyPasswordRequestDto request) {
        adminService.changeMyPassword(request);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();
    }



    @GetMapping(path = "/check_nickname/{nickname}")
    @Operation(summary = "어드민 닉네임 중복 확인", description = "어드민 닉네임 중복을 확인합니다.")
    public CommonResponse<Object> checkAdminNickname(@PathVariable("nickname") String nickname) {
        boolean isDuplicate = adminService.checkAdminNickname(nickname);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(isDuplicate)
            .build();
    }

    @PostMapping(path = "/reset/pw/{id}")
    @Operation(summary = "비밀번호 초기화", description = "비밀번호를 초기화합니다.")
    public CommonResponse<Object> resetPassword(@PathVariable("id") Long id) {
        adminService.resetPassword(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();
    }

    // 어드민 아이디 중복 확인
    @GetMapping(path = "/check_admin_id/{adminId}")
    @Operation(summary = "어드민 아이디 중복 확인", description = "어드민 아이디 중복을 확인합니다.")
    public CommonResponse<Object> checkAdminId(@PathVariable("adminId") String adminId) {
        boolean isDuplicate = adminService.checkAdminId(adminId);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(isDuplicate)
            .build();
    }

    // 어드민 인트로 이미지 비동기 삭제(단일)
    @DeleteMapping(path = "/intro-image/{id}")
    @Operation(summary = "어드민 인트로 이미지 삭제", description = "어드민 인트로 이미지를 삭제합니다.")
    public CommonResponse<Object> deleteIntroImage(@PathVariable("id") Long id) {
        adminService.deleteIntroImage(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();

    }

    // 어드민 프로필 이미지 비동기 삭제
    @DeleteMapping(path = "/profile-image/{id}")
    @Operation(summary = "어드민 프로필 이미지 삭제", description = "어드민 프로필 이미지를 삭제합니다.")
    public CommonResponse<Object> deleteProfileImage(@PathVariable("id") Long id) {
        adminService.deleteProfileImage(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();
    }


}
