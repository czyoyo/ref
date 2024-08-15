package com.example.ref.entity;

import com.example.ref.request.admin.user.ModifyUserRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Comment("유저")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("인덱스")
    private long id;

    // 회원 탈퇴일
    // 회원 탈퇴일이 지난 회원은 로그인이 불가능하도록 수정
    // 회원 탈퇴일 3개월 후 회원 정보를 삭제하는 로직을 쿼츠로 구현
    @Comment("회원 탈퇴일")
    @Column(name = "leave_date")
    @Builder.Default
    private LocalDateTime leaveDate = null;

    @Column(name="user_id", unique = true, nullable = false, columnDefinition = "VARCHAR(255)")
    @Comment("유저 아이디")
    private String userId;

    @Column(name="password", columnDefinition = "VARCHAR(255)")
    @Comment("유저 비밀번호")
    private String password;

    @Column(name="nickname", columnDefinition = "VARCHAR(255)", unique = true)
    @Comment("유저 닉네임")
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", columnDefinition = "BIGINT UNSIGNED", nullable = true, foreignKey = @ForeignKey(name = "FK_USER_ADMIN_ID"))
    @Comment("담당 어드민 인덱스")
    private Admin admin;

    @Column(name="memo", columnDefinition = "TEXT")
    @Comment("유저 메모")
    private String memo;

    @Column(name="approved_level", columnDefinition = "TINYINT", nullable = false)
    @Comment("유저 승인 레벨")
    @Builder.Default
    private int approvedLevel = 0;

    @Column(name="admin_change_level", columnDefinition = "TINYINT")
    @Comment("어드민 변경 레벨")
    @Builder.Default
    private Integer adminChangeLevel = null;

    @Column(name="type_inspection", columnDefinition = "VARCHAR(255)")
    @Comment("외부 검사 여부")
    private String typeInspection;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_category_id", columnDefinition = "BIGINT UNSIGNED", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_CATEGORY_ID"))
    @Comment("카테고리 인덱스")
    private UserCategory userCategory;

    @Comment("유저 리프레시 토큰")
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    @Comment("프로필 이미지")
    private UserProfileImage userProfileImage;

    public void modifyUser(ModifyUserRequestDto modifyUserRequestDto) {
        if(StringUtils.hasText(modifyUserRequestDto.getNickname())){ // 닉네임은 변경되지 않았을 때만 변경 공백이나 null 불가능
            this.nickname = modifyUserRequestDto.getNickname();
        }
        this.memo = modifyUserRequestDto.getMemo();
    }

    public void withdrawUser(UserCategory userCategory) {
        this.userCategory = userCategory;
        this.typeInspection = null;
        this.refreshToken = null;
        this.password = null;
        this.memo = null;
        this.deleteAdmin();
    }

    public void deleteAdmin() {
        this.admin = null;
    }

    public void setUserCategory(UserCategory userCategory) {
        if(this.userCategory != null) {
            this.userCategory.getUserList().remove(this);
        }
        this.userCategory = userCategory;
        userCategory.getUserList().add(this);
    }

    public void setAdminChangeLevel(Integer adminChangeLevel) {this.adminChangeLevel = adminChangeLevel;}
    public void setPassword(String password) {
        this.password = password;
    }

    public void setLeaveDate(LocalDateTime leaveDate) {
        this.leaveDate = leaveDate;
    }

    public void setApprovedLevel(int approvedLevel) {
        this.approvedLevel = approvedLevel;
    }

    public void setUserAdmin(Admin admin) {
        this.admin = admin;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }



}
