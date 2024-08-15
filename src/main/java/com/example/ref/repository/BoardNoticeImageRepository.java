package com.example.ref.repository;

import com.example.ref.entity.BoardNotice;
import com.example.ref.entity.BoardNoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardNoticeImageRepository extends JpaRepository<BoardNoticeImage, Long> {

    void deleteByBoardNoticeAndType(BoardNotice boardNotice, String type);
    BoardNoticeImage findByBoardNoticeAndType(BoardNotice boardNotice, String type);

}
