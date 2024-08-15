package com.example.ref.request.admin.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class ModifyUserRequestDto {

    // 유저 인덱스
    @NotNull(message = "필수 데이터가 없습니다.")
    @Schema(description = "유저 인덱스", example = "1")
    private Long userIdx;
    // 카테고리 인덱스
    @NotNull(message = "카테고리를 선택해주세요.")
    @Schema(description = "카테고리 인덱스", example = "1")
    private Long userCategoryIdx;
    // 활동레벨
    @Schema(description = "활동레벨", example = "1")
    private Integer adminChangeLevel;
    // 담당 어드민 인덱스
    @NotNull(message = "어드민을 선택해주세요.")
    @Schema(description = "어드민 인덱스", example = "1")
    private Long adminIdx;
    // 닉네임
    @Schema(description = "닉네임", example = "찰리박", nullable = true)
    private String nickname;
    // 메모
    @Schema(description = "메모", example = "메모", nullable = true)
    private String memo;

    @Schema(description = "프로필 이미지", example = "이미지 파일")
    private MultipartFile profileImage;

}
