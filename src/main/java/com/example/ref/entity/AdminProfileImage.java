package com.example.ref.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "admin_profile_image")
public class AdminProfileImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("인덱스")
    private long id;

    @Column(name = "file_extension", columnDefinition = "VARCHAR(255)")
    @Comment("파일 확장자")
    private String fileExtension;

    @JoinColumn(name = "admin_id", columnDefinition = "BIGINT UNSIGNED", nullable = false, foreignKey = @ForeignKey(name = "FK_ADMIN_PROFILE_IMAGE_ADMIN_ID"))
    @OneToOne(fetch = FetchType.LAZY)
    @Comment("어드민 인덱스")
    private Admin admin;

    @Column(name = "file_path", columnDefinition = "VARCHAR(255)")
    @Comment("파일 경로")
    private String filePath;

    @Column(name = "origin_file_name", columnDefinition = "VARCHAR(255)")
    @Comment("원본 파일 이름")
    private String originFileName;

    @Column(name = "saved_file_name", columnDefinition = "VARCHAR(255)")
    @Comment("저장된 파일 이름")
    private String savedFileName;

    @Column(name = "url", columnDefinition = "VARCHAR(255)")
    @Comment("URL")
    private String url;

}
