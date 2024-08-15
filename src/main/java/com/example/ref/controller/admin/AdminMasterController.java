package com.example.ref.controller.admin;

import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/admin/delete")
public class AdminMasterController {

    private final AdminService adminService;

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "어드민 삭제", description = "어드민을 삭제합니다.")
    public CommonResponse<Object> deleteAdmin(@PathVariable("id") Long id) {
        adminService.deleteAdmin(id);
        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();
    }

}
