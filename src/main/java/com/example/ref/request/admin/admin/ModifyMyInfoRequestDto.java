package com.example.ref.request.admin.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ModifyMyInfoRequestDto {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Schema(description = "닉네임", example = "관리자")
    private String nickname;

    @NotNull(message = "어드민 메모를 입력해주세요.(공백 이라도)")
    @Schema(description = "어드민 메모", example = "어드민 메모입니다.")
    private String memo;

    @Schema(description = "프로필 이미지", nullable = true)
    private MultipartFile profileImage;

    @Schema(description = "소개 이미지1", nullable = true)
    private MultipartFile introImage01;

    @Schema(description = "소개 이미지2", nullable = true)
    private MultipartFile introImage02;

}

