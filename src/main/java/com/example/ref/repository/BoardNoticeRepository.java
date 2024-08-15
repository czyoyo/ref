package com.example.ref.repository;

import com.example.ref.entity.BoardNotice;
import com.example.ref.entity.NoticeCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardNoticeRepository extends JpaRepository<BoardNotice, Long>, BoardNoticeCustomRepository {


    // notice + Image
    @Query("select b from BoardNotice b left join fetch b.boardNoticeImageList where b.id = :id")
    BoardNotice findBoardNoticeAndImageById(@Param("id") Long id);

    // 최신 순으로 10개만 가져오기 limit 10
    @Query("select b from BoardNotice b order by b.id desc limit 10")
    List<BoardNotice> findTop10ByOrderByIdDesc();

    // 최신 순으로 다 가져오기
    @Query("select b from BoardNotice b order by b.id desc")
    List<BoardNotice> findAllByOrderByIdDesc();

    boolean existsByNoticeCategory(NoticeCategory noticeCategory);

}
