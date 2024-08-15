package com.example.ref.controller.admin;

import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/admin/users")
public class UserMasterController {

    private final UserService userService;

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "유저 삭제", description = "유저를 삭제합니다.")
    public CommonResponse<Object> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }

}
