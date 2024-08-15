package com.example.ref.controller.user;

import com.example.ref.dto.UserDto;
import com.example.ref.request.user.user.LoginClientUserRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.response.JwtResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.user.UserClientService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/u/users")
@Slf4j
public class UserClientController {

    private final UserClientService userClientService;


    @PostMapping(path = "/guest/login")
    @Operation(summary = "게스트 로그인", description = "게스트로 로그인합니다.")
    public ResponseEntity<CommonResponse<JwtResponse>> guestLogin(HttpServletRequest request) {

        // redis 저장 구조
        // id : guest_account:ip_device
        // value : ip_device
        return userClientService.guestLogin(request);
    }

    @PostMapping(path = "/login")
    @Operation(summary = "로그인", description = "로그인합니다.")
    public ResponseEntity<CommonResponse<JwtResponse>> login(@RequestBody @Valid
    LoginClientUserRequestDto loginClientUserRequestDto) {
        return userClientService.login(loginClientUserRequestDto);
    }

    @GetMapping(path = "/my-info")
    @Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
    public CommonResponse<Object> getMyInfo() {
        UserDto myInfo = userClientService.getMyInfo();
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(myInfo)
            .build();
    }



}
