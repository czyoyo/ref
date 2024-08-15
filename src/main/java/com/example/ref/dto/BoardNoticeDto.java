package com.example.ref.dto;


import com.example.ref.entity.Admin;
import com.example.ref.entity.BoardNotice;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardNoticeDto {

    @Schema(description = "공지사항 인덱스", example = "1")
    private Long id;

    @Schema(description = "공지사항 제목", example = "제목")
    private String title;

    @Schema(description = "공지사항 내용", example = "내용")
    private String content;

    @Schema(description = "작성자(어드민)")
    private AdminDto admin;

    @Schema(description = "업로드 이미지 리스트")
    private List<BoardNoticeImageDto> boardNoticeImageList;

    @Schema(description = "게시판 색상")
    private BoardColorDto boardColor;

    @Schema(description = "공지사항 카테고리")
    private NoticeCategoryDto noticeCategory;


    private static BoardNoticeDto.BoardNoticeDtoBuilder boardNoticeDtoBuilder(BoardNotice boardNotice) {
        return BoardNoticeDto.builder()
            .id(boardNotice.getId())
            .title(boardNotice.getTitle())
            .content(boardNotice.getContent());
    }

    private static NoticeCategoryDto noticeCategoryDto(BoardNotice boardNotice) {
        if(boardNotice.getNoticeCategory() == null) {
            return null;
        }
        return NoticeCategoryDto.builder()
            .id(boardNotice.getNoticeCategory().getId())
            .title(boardNotice.getNoticeCategory().getTitle())
            .memo(boardNotice.getNoticeCategory().getMemo())
            .build();
    }

    private static BoardColorDto boardColorDto(BoardNotice boardNotice) {
        if(boardNotice.getBoardColor() == null) {
            return null;
        }
        return BoardColorDto.builder()
            .id(boardNotice.getBoardColor().getId())
            .title(boardNotice.getBoardColor().getTitle())
            .color(boardNotice.getBoardColor().getColor())
            .build();
    }



    private static AdminDto adminDto(BoardNotice boardNotice) {

        if(boardNotice.getAdmin() == null) {
            return null;
        }

        Admin admin = boardNotice.getAdmin();
        return AdminDto.builder()
            .id(admin.getId())
            .adminId(admin.getAdminId())
            .nickname(admin.getNickname())
            .adminAbout(admin.getAdminAbout())
            .memo(admin.getMemo())
            .build();
    }

    private static List<BoardNoticeImageDto> boardNoticeImagesDtoList(BoardNotice boardNotice) {
        return Optional.ofNullable(boardNotice.getBoardNoticeImageList())
            .map(boardNoticeImages -> boardNoticeImages.stream()
                .map(boardNoticeImage -> BoardNoticeImageDto.builder()
                    .id(boardNoticeImage.getId())
                    .originFileName(boardNoticeImage.getOriginFileName())
                    .savedFileName(boardNoticeImage.getSavedFileName())
                    .fileExtension(boardNoticeImage.getFileExtension())
                    .filePath(boardNoticeImage.getFilePath())
                    .url(boardNoticeImage.getUrl())
                    .build())
                .toList())
            .orElse(Collections.emptyList());
    }

    public static BoardNoticeDto convertToDto(BoardNotice boardNotice) {
        BoardNoticeDtoBuilder builder = boardNoticeDtoBuilder(boardNotice);

        builder
            .admin(adminDto(boardNotice))
            .boardNoticeImageList(boardNoticeImagesDtoList(boardNotice))
            .noticeCategory(noticeCategoryDto(boardNotice))
            .boardColor(boardColorDto(boardNotice));

        return builder.build();
    }

}
