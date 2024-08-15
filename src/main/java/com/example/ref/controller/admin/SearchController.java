package com.example.ref.controller.admin;

import com.example.ref.elasticsearch.entity.SearchHistoryDocument;
import com.example.ref.request.admin.search.AddSearchKeywordRequestDto;
import com.example.ref.request.admin.search.GetRelatedSearchKeywordRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/api/admin/search")
public class SearchController {

    private final SearchService searchService;

    // TODO: 내 검색어 등록 Redis 의 내 검색어랑 Elasticsearch 의 인기검색어, 연관검색어 등록
//    @PostMapping(path = "/add-my-search")
//    public CommonResponse<Object> allClearSearchKeyword(@RequestBody @Valid AddMySearchRequest addMySearchRequest) {
//
//        searchService.addMySearchKeywords(addMySearchRequest);
//
//        return CommonResponse.builder()
//            .code(ResponseCode.SUCCESS.getCode())
//            .status(ResponseCode.SUCCESS.getStatus())
//            .build();
//    }

    // TODO: 인기 검색어 조회하며 Elasticsearch 의 인기검색어
    @GetMapping(path = "/get-popular-search-keyword")
    @Operation(summary = "인기 검색어 조회", description = "인기 검색어 10개를 조회합니다.")
    public CommonResponse<Object> getPopularSearchKeyword() {

        Page<SearchHistoryDocument> popularSearchKeyword = searchService.getPopularSearchKeyword();

        return CommonResponse.builder()
            .data(popularSearchKeyword)
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .build();
    }


    @PostMapping(path = "/add-search-keyword")
    @Operation(summary = "검색어 등록", description = "검색어를 등록합니다.")
    public CommonResponse<Object> addSearchKeyword(@RequestBody @Valid AddSearchKeywordRequestDto addSearchKeywordRequestDto) {

        searchService.addSearchKeyword(addSearchKeywordRequestDto);

        return CommonResponse.builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .build();
    }


    // TODO: 연관 검색어 조회하며 Elasticsearch 의 연관검색어
    @GetMapping(path = "/get-related-search-keyword")
    @Operation(summary = "유사 검색어 조회", description = "연관 검색어를 조회합니다.")
    public CommonResponse<Object> getRelatedSearchKeyword(@ModelAttribute @Valid
    GetRelatedSearchKeywordRequestDto getRelatedSearchKeywordRequestDto) {

        Page<SearchHistoryDocument> relatedSearchKeyword = searchService.getRelatedSearchKeyword(
            getRelatedSearchKeywordRequestDto);

        return CommonResponse.builder()
            .data(relatedSearchKeyword)
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .build();

    }

    // TODO: 내 검색어 조회 하며 Redis 에 저장된 내 검색어
    @GetMapping(path = "/get-my-search-keyword")
    @Operation(summary = "내 검색어 조회", description = "내 검색어를 조회합니다.")
    public CommonResponse<Object> getMySearchKeyword() {

        Set<Object> mySearchKeywords = searchService.getMySearchKeywords();

        return CommonResponse.builder()
            .data(mySearchKeywords)
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .build();

    }




}
