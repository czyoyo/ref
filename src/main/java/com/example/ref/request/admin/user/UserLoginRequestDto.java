package com.example.ref.request.admin.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserLoginRequestDto {

    @Schema(description = "유저 아이디", example = "userId")
    private String userId;

    @Schema(description = "비밀번호", example = "password")
    private String password;


}
