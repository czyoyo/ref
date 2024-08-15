package com.example.ref.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "search_history")
@Comment("검색어 저장")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SearchHistory extends BaseEntity {

    @Id
    @Comment("인덱스")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Comment("검색어")
    @Column(name = "search_word", nullable = false)
    private String searchWord;

    @Comment("검색 횟수")
    @Setter
    @Builder.Default
    @Column(name = "search_count", columnDefinition = "BIGINT default 0", nullable = false)
    private long searchCount = 0;

}
