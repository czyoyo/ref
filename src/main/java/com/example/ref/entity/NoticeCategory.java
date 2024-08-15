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
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "notice_category")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("인덱스")
    private long id;

    @Column(name = "title", columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    @Comment("카테고리명")
    private String title;

    @Column(name = "memo" , columnDefinition = "TEXT")
    @Comment("메모")
    private String memo;


    public void modifyNoticeCategory(String title, String memo) {
        this.title = title;
        this.memo = memo;
    }



}
