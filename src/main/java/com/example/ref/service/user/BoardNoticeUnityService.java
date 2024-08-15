package com.example.ref.service.user;

import com.example.ref.dto.BoardNoticeDto;
import com.example.ref.entity.BoardNotice;
import com.example.ref.repository.BoardNoticeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardNoticeUnityService {

    private final BoardNoticeRepository boardNoticeRepository;


    public BoardNoticeDto getBoardNotice(Long id) {

        BoardNotice boardNotice = boardNoticeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        return BoardNoticeDto.builder()
            .id(boardNotice.getId())
            .title(boardNotice.getTitle())
            .content(boardNotice.getContent())
            .build();
    }

    public List<BoardNoticeDto> getBoardNoticeList() {

        List<BoardNotice> boardNoticeList = boardNoticeRepository.findAllByOrderByIdDesc();

        return
            boardNoticeList.stream()
                .map(boardNotice -> BoardNoticeDto.builder()
                    .id(boardNotice.getId())
                    .title(boardNotice.getTitle())
                    .content(boardNotice.getContent())
                    .build())
                .toList();
    }





}
