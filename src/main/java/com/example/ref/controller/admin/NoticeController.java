package com.example.ref.controller.admin;

import com.example.ref.dto.BoardNoticeDto;
import com.example.ref.entity.BoardColor;
import com.example.ref.request.admin.board.notice.AddBoardNoticeRequestDto;
import com.example.ref.request.admin.board.notice.GetBoardNoticeListRequestDto;
import com.example.ref.request.admin.board.notice.ModifyBoardNoticeRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.BoardNoticeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/admin/boards/notice")
public class NoticeController {

    private final BoardNoticeService boardNoticeService;

    @Operation(summary = "공지사항 등록", description = "공지사항을 등록합니다.")
    @PostMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public CommonResponse<Object> addNotice(@ModelAttribute @Valid AddBoardNoticeRequestDto addBoardNoticeRequestDto)
        throws IOException {

        boardNoticeService.addBoard(addBoardNoticeRequestDto);

        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();
    }

    @GetMapping(path = "/color")
    @Operation(summary = "공지사항 색상 목록 조회", description = "공지사항 색상 목록을 조회합니다.")
    public CommonResponse<Object> getNoticeColorList() {

        List<BoardColor> boardColorList = boardNoticeService.getBoardColorList();

        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(boardColorList)
            .build();
    }

    @GetMapping(path = "")
    @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록을 조회합니다.")
    public CommonResponse<Page<BoardNoticeDto>> getNoticeList(@ModelAttribute @Valid GetBoardNoticeListRequestDto getBoardNoticeListRequestDto, Pageable pageable) {

        Page<BoardNoticeDto> boardList = boardNoticeService.getBoardList(pageable,
            getBoardNoticeListRequestDto);

        return CommonResponse.<Page<BoardNoticeDto>>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(boardList)
            .build();

    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 상세를 조회합니다.")
    public CommonResponse<BoardNoticeDto> getNotice(@PathVariable("id") Long id) {

        BoardNoticeDto board = boardNoticeService.getBoard(id);

        return CommonResponse.<BoardNoticeDto>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(board)
            .build();

    }

    @PatchMapping(path = "")
    @Operation(summary = "공지사항 수정", description = "공지사항을 수정합니다.")
    public CommonResponse<Object> modifyNotice(@ModelAttribute @Valid ModifyBoardNoticeRequestDto modifyBoardNoticeRequestDto)
        throws IOException {

        boardNoticeService.modifyBoard(modifyBoardNoticeRequestDto);

        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();

    }

    @DeleteMapping(path="/image/{id}")
    @Operation(summary = "공지사항 이미지 삭제", description = "공지사항 이미지를 삭제합니다.")
    public CommonResponse<Object> deleteNoticeImage(@PathVariable("id") Long id) {

        boardNoticeService.deleteBoardImage(id);

        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();

    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.")
    public CommonResponse<Object> deleteNotice(@PathVariable("id") Long id)
        throws JsonProcessingException {

        boardNoticeService.deleteBoard(id);

        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(null)
            .build();

    }







}
