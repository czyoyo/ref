package com.example.ref.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "board_notice_image")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Comment("공지사항 이미지")
public class BoardNoticeImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("인덱스")
    private long id;

    @JoinColumn(name = "board_id", columnDefinition = "BIGINT UNSIGNED", nullable = false, foreignKey = @ForeignKey(name = "FK_BOARD_NOTICE_IMAGE_BOARD_ID"))
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("공지사항 게시글 인덱스")
    private BoardNotice boardNotice;

    @Column(name = "origin_file_name", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("원본 파일명")
    private String originFileName;

    @Column(name = "saved_file_name", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("저장 파일명")
    private String savedFileName;

    @Column(name = "file_extension", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("파일 확장자")
    private String fileExtension;

    @Column(name = "file_path", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("파일 경로")
    private String filePath;

    @Column(name = "url", columnDefinition = "VARCHAR(255)")
    @Comment("파일 URL")
    private String url;

    @Column(name = "type", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("공지사항 타입")
    private String type;


    public void setBoardNotice(BoardNotice boardNotice) {
        if(this.boardNotice != null) {
            this.boardNotice.getBoardNoticeImageList().remove(this);
        }

        this.boardNotice = boardNotice;
        boardNotice.getBoardNoticeImageList().add(this);
    }

    public void removeBoardNotice() {
        if(this.boardNotice != null) {
            this.boardNotice.getBoardNoticeImageList().remove(this);
        }
        this.boardNotice = null;
    }

}
