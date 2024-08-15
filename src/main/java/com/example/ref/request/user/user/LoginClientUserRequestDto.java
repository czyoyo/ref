package com.example.ref.request.user.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginClientUserRequestDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Schema(description = "유저 아이디", example = "userId")
    private String userId;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "비밀번호", example = "password")
    private String password;

}
