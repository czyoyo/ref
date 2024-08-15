package com.example.ref.elasticsearch.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetContainingKeywordsRequest {

    private String searchKeyword;

}
