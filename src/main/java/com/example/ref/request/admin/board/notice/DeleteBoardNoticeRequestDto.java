package com.example.ref.request.admin.board.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeleteBoardNoticeRequestDto {

    @Schema(description = "공지사항 인덱스", example = "1")
    private Long boardNoticeIdx;

}
