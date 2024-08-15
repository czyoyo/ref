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
@Table(name = "board_color")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Comment("게시판 색상")
public class BoardColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("인덱스")
    private long id;

    @Column(name="title", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("제목")
    private String title;

    @Column(name="color", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("색상")
    private String color;

}
