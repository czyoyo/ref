package com.example.ref.request.admin.board.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 등록 요청")
public class AddBoardNoticeRequestDto {


    @NotNull(message = "필수값이 없습니다.")
    @Schema(description = "카테고리 인덱스", example = "1")
    private Long categoryIdx;

    @Schema(description = "공지사항 색상 인덱스", example = "1")
    private Long colorIdx;

    @NotBlank(message = "제목을 입력해주세요.")
    @Schema(description = "제목", example = "제목 입니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Schema(description = "내용", example = "내용 입니다.")
    private String content;

    @Schema(description = "이미지 파일 01", nullable = true)
    private MultipartFile imageFile01;
    @Schema(description = "이미지 파일 02", nullable = true)
    private MultipartFile imageFile02;
    @Schema(description = "이미지 파일 03", nullable = true)
    private MultipartFile imageFile03;
    @Schema(description = "이미지 파일 04", nullable = true)
    private MultipartFile imageFile04;
    @Schema(description = "이미지 파일 05", nullable = true)
    private MultipartFile imageFile05;

}
