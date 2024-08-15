package com.example.ref.controller.user;

import com.example.ref.dto.BoardNoticeDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.user.BoardNoticeUnityService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/user/boards/notice")
public class NoticeClientController {

    private final BoardNoticeUnityService boardNoticeUnityService;


    @GetMapping(path = "")
    @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록을 조회합니다.")
    public CommonResponse<List<BoardNoticeDto>> getNoticeList() {

        List<BoardNoticeDto> boardList = boardNoticeUnityService.getBoardNoticeList();

        return CommonResponse.<List<BoardNoticeDto>>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(boardList)
            .build();
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 상세를 조회합니다.")
    public CommonResponse<BoardNoticeDto> getNotice(@PathVariable("id") Long id) {

        BoardNoticeDto boardNotice = boardNoticeUnityService.getBoardNotice(id);

        return CommonResponse.<BoardNoticeDto>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(boardNotice)
            .build();
    }




}
