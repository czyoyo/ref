package com.example.ref.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "visitor")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Comment("방문자")
public class Visitor extends BaseEntity {

    @Id
    @Comment("인덱스")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private long id;

    @Comment("아이피")
    @Column(name = "ip", columnDefinition = "VARCHAR(255)")
    private String ip;

    @Comment("유저 에이전트")
    @Column(name = "user_agent", columnDefinition = "VARCHAR(255)")
    private String userAgent;

    @Comment("방문일")
    @Column(name = "visit_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDate;

}
