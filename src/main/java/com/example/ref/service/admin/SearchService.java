package com.example.ref.service.admin;

import com.example.ref.elasticsearch.entity.SearchHistoryDocument;
import com.example.ref.elasticsearch.repository.SearchHistoryDocumentElasticsearchRepository;
import com.example.ref.request.admin.search.AddSearchKeywordRequestDto;
import com.example.ref.request.admin.search.GetRelatedSearchKeywordRequestDto;
import com.example.ref.rules.RedisType;
import com.example.ref.util.AuthUtils;
import com.example.ref.util.RedisUtils;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SearchService {

    private final SearchHistoryDocumentElasticsearchRepository searchHistoryDocumentElasticsearchRepository;
    private final RedisUtils redisUtils;




    public Page<SearchHistoryDocument> getRelatedSearchKeyword(
        GetRelatedSearchKeywordRequestDto getRelatedSearchKeywordRequestDto) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "searchCount"));

        // Elasticsearch 에서 연관검색어 10개 가져오기
        return searchHistoryDocumentElasticsearchRepository.findTop10BySearchKeywordStartingWith(
            getRelatedSearchKeywordRequestDto.getSearchKeyword(), pageable);

    }

    public void addSearchKeyword(AddSearchKeywordRequestDto request) {

        String userId = AuthUtils.getUserId();

        // Redis 에 내 검색어 저장
        addMySearchKeywordToRedis(userId, request.getSearchKeyword());

        // Elasticsearch 에 검색어 저장
        addSearchKeywordToElasticsearch(request.getSearchKeyword());
    }




    public Page<SearchHistoryDocument> getPopularSearchKeyword() {

        // pageable 에 sort 를 추가하여 검색어 순으로 정렬
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "searchCount"));

        // Elasticsearch 에서 인기검색어 10개 가져오기
        return searchHistoryDocumentElasticsearchRepository.getPopularSearchKeyword(pageable);
    }

    public Set<Object> getMySearchKeywords() {
        // 토큰으로 부터 인증 객체를 가져온다.
        String userId = AuthUtils.getUserId();
        // 내 검색어 가져오기
        return redisUtils.getSetValue(userId, RedisType.SEARCH.getType());
    }


    private void addSearchKeywordToElasticsearch(String searchKeyword) {
        // 검색어랑 정확히 일치하는 데이터가 있는지 확인
        SearchHistoryDocument bySearchKeywordEquals = searchHistoryDocumentElasticsearchRepository.findByIdentifierEquals(searchKeyword);
        // 없으면 새로 저장
        if(bySearchKeywordEquals == null) {
            SearchHistoryDocument build = SearchHistoryDocument.builder()
                .searchKeyword(searchKeyword)
                .identifier(searchKeyword)
                .searchCount(1L)
                .build();
            searchHistoryDocumentElasticsearchRepository.save(build);
        } else {
            // 있으면 searchCount 를 1 증가
            addSearchCount(bySearchKeywordEquals);
        }
    }


    private void addSearchCount(SearchHistoryDocument searchHistoryDocument) {
        searchHistoryDocument.setSearchCount(searchHistoryDocument.getSearchCount() + 1);
        searchHistoryDocumentElasticsearchRepository.save(searchHistoryDocument);
    }


    private void addMySearchKeywordToRedis(String userId, String searchKeyword) {
        // 3달의 MS
        int expire = 60 * 60 * 24 * 30 * 3;

        Set<Object> setValue = redisUtils.getSetValue(userId, RedisType.SEARCH.getType());

        // 50개 이상이면 50개 까지 오래된 순으로 삭제
        while (setValue.size() >= 10) {
            Object[] objects = setValue.toArray();
            redisUtils.deleteSingleSetValue(userId, objects[0].toString(), RedisType.SEARCH.getType());
            setValue = redisUtils.getSetValue(userId, RedisType.SEARCH.getType());
        }

        redisUtils.addValueWithPrefixAndTimeUnit(userId, searchKeyword, RedisType.SEARCH.getType(), expire,
            TimeUnit.SECONDS);
    }


}





