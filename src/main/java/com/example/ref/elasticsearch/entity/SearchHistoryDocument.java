package com.example.ref.elasticsearch.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(indexName = "search_history", createIndex = true)
@Builder
@Setter
@Mapping(mappingPath = "/static/elastic/search_history_mapping.json")
@Setting(settingPath = "/static/elastic/search-history-setting.json")
public class SearchHistoryDocument {

    @Id
    private String id;

    private String identifier;

    private String searchKeyword;

    private Long searchCount;

}
