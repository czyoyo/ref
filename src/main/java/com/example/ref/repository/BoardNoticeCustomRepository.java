package com.example.ref.repository;

import com.example.ref.entity.BoardNotice;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardNoticeCustomRepository {

    Page<BoardNotice> findAllByTitleContainingAndCreatedAtBetween(Pageable pageable, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
