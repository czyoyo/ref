package com.example.ref.service.admin;

import com.example.ref.dto.NoticeCategoryDto;
import com.example.ref.entity.NoticeCategory;
import com.example.ref.repository.BoardNoticeRepository;
import com.example.ref.repository.NoticeCategoryRepository;
import com.example.ref.request.admin.category.notice.AddNoticeCategoryRequestDto;
import com.example.ref.request.admin.category.notice.GetNoticeCategoryListRequestDto;
import com.example.ref.request.admin.category.notice.ModifyNoticeCategoryRequestDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoticeCategoryService {

    private final NoticeCategoryRepository noticeCategoryRepository;
    private final BoardNoticeRepository boardNoticeRepository;


    // 공지사항 카테고리 추가
    public void addNoticeCategory(AddNoticeCategoryRequestDto request) {

        // 타이틀 중복 확인
        if (noticeCategoryRepository.existsByTitle(request.getTitle())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }

        NoticeCategory noticeCategory = NoticeCategory.builder()
                .title(request.getTitle())
                .memo(request.getMemo())
                .build();

        noticeCategoryRepository.save(noticeCategory);
    }

    // 공지사항 카테고리 수정
    public void modifyNoticeCategory(ModifyNoticeCategoryRequestDto request) {

        NoticeCategory noticeCategory = noticeCategoryRepository.findById(
            request.getNoticeCategoryIdx()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        noticeCategory.modifyNoticeCategory(request.getTitle(), request.getMemo());

    }

    // 공지사항 카테고리 삭제
    public void deleteNoticeCategory(Long noticeCategoryIdx) {

        NoticeCategory noticeCategory = noticeCategoryRepository.findById(noticeCategoryIdx)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 공지사항에 적용된 카테고리가 있는지 확인
        if (boardNoticeRepository.existsByNoticeCategory(noticeCategory)) {
            throw new IllegalArgumentException("공지사항에 적용된 카테고리가 이미 존재 합니다.");
        }

        noticeCategoryRepository.delete(noticeCategory);

    }

    // 공지사항 카테고리 리스트 조회
    // AOP 무시
    public Page<NoticeCategoryDto> getNoticeCategoryList(Pageable pageable, GetNoticeCategoryListRequestDto request) {


        LocalDateTime startDateTime = request.getStartDate() != null ? request.getStartDate().atTime(0, 0, 0) : null;
        LocalDateTime endDateTime = request.getEndDate() != null ? request.getEndDate().atTime(23, 59, 59) : null;

        Page<NoticeCategory> pageableList = noticeCategoryRepository.findAllByTitleContainingAndCreatedAtBetweenStartDateTimeAndEndDateTime(
            pageable, request.getKeyword(), startDateTime, endDateTime
        );

        return pageableList.map(NoticeCategoryDto::convertToDto);
    }

    // 공지사항 카테고리 상세 조회
    public NoticeCategoryDto getNoticeCategoryDetail(Long id) {

        NoticeCategory noticeCategory = noticeCategoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        return NoticeCategoryDto.convertToDto(noticeCategory);
    }




}
