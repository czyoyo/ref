package com.example.ref.request.admin.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class AddUserRequestDto {

    @NotNull(message = "카테고리를 선택해주세요.")
    @Schema(description = "카테고리 인덱스", example = "1")
    private Long userCategoryIdx;

    @NotNull(message = "활동레벨을 선택해주세요.")
    @Schema(description = "활동레벨", example = "1")
    private Integer approvedLevel;

    @NotNull(message = "어드민을 선택해주세요.")
    @Schema(description = "어드민 인덱스", example = "1")
    private Long adminIdx;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Schema(description = "닉네임", example = "찰리박")
    private String nickname;

    @NotBlank(message = "아이디를 입력해주세요.")
    @Schema(description = "아이디", example = "charlie")
    private String userId;

    @Schema(description = "메모", example = "메모")
    private String memo;

    @Schema(description = "프로필 이미지", example = "이미지 파일")
    private MultipartFile profileImage;
}
