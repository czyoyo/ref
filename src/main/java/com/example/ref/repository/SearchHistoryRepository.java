package com.example.ref.repository;

import com.example.ref.entity.SearchHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    boolean existsBySearchWord(String searchWord);

    SearchHistory findBySearchWord(String searchWord);

    // 인기검색어 10개 조회
    List<SearchHistory> findTop10ByOrderBySearchCountDesc();
}
