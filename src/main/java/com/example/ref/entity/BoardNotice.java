package com.example.ref.entity;

import com.example.ref.request.admin.board.notice.ModifyBoardNoticeRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "board_notice")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Comment("공지사항")
public class BoardNotice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("인덱스")
    private long id;

    @Column(name="title", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("제목")
    private String title;

    @Column(name="content", columnDefinition = "TEXT")
    @Comment("내용")
    private String content;

    @JoinColumn(name = "writer_id", columnDefinition = "BIGINT UNSIGNED", nullable = false, foreignKey = @ForeignKey(name = "FK_BOARD_NOTICE_ADMIN_ID"))
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("어드민 인덱스")
    private Admin admin;

    @OneToMany(mappedBy = "boardNotice", fetch = FetchType.LAZY)
    @Comment("공지사항 이미지")
    private List<BoardNoticeImage> boardNoticeImageList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_category_id", columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "FK_BOARD_NOTICE_NOTICE_CATEGORY_ID"))
    @Comment("공지사항 카테고리 인덱스")
    private NoticeCategory noticeCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_color_id", columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "FK_BOARD_NOTICE_BOARD_COLOR_ID"))
    @Comment("공지사항 색상 인덱스")
    private BoardColor boardColor;

    public void modifyBoardNotice(ModifyBoardNoticeRequestDto modifyBoardNoticeRequestDto){
        this.title = modifyBoardNoticeRequestDto.getTitle();
        this.content = modifyBoardNoticeRequestDto.getContent();
    }

    public void setNoticeCategory(NoticeCategory noticeCategory) {
        this.noticeCategory = noticeCategory;
    }

    public void setBoardColor(BoardColor boardColor) {
        this.boardColor = boardColor;
    }

}
