package com.example.ref.elasticsearch.repository;

import com.example.ref.elasticsearch.entity.SearchHistoryDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchHistoryDocumentElasticsearchRepository extends ElasticsearchRepository<SearchHistoryDocument, Long> {


    // 검색어로 검색
//    List<SearchHistoryDocument> findBySearchKeyword(String searchKeyword);


    // 검색어로 검색한 정확히 일치하는 검색어 가져오기
    SearchHistoryDocument findByIdentifierEquals(String searchKeyword);
    SearchHistoryDocument findByIdentifier(String searchKeyword);
    SearchHistoryDocument findByIdentifierContaining(String searchKeyword);


    // 인기검색어 10개 가져오기 search_history 에서 point 높은 순으로 가져옴
    @Query("{\"bool\" : {\"must\" : {\"match_all\" : {}},\"must_not\" : [],\"filter\" : [],\"should\" : []}}")
    Page<SearchHistoryDocument> getPopularSearchKeyword(Pageable pageable);


    // 접두사 검색, 띄어쓰기 포함 10개 가져오기, search_history 에서 searchKeyword 으로 시작하는 10개 가져옴
    @Query("{\"bool\": {\"must\": [{\"prefix\": {\"searchKeyword\": \"?0\"}}],\"must_not\": [],\"should\": []}}")
    Page<SearchHistoryDocument> findTop10BySearchKeywordStartingWith(String searchKeyword, Pageable pageable);


}
