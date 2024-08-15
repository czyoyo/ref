package com.example.ref.entity;

import com.example.ref.request.admin.admin.ModifyAdminRequestDto;
import com.example.ref.request.admin.admin.ModifyMyInfoRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "admin")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Comment("어드민")
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("인덱스")
    private long id;

    @Column(name="admin_id", columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    @Comment("어드민 아이디")
    private String adminId;

    @Column(name="nickname", columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    @Comment("어드민")
    private String nickname;

    @Column(name="admin_about", columnDefinition = "TEXT")
    @Comment("어드민")
    private String adminAbout;

    @Column(name="memo", columnDefinition = "TEXT")
    @Comment("어드민")
    private String memo;

    @Column(name="password", columnDefinition = "VARCHAR(255)", nullable = true)
    @Comment("어드민")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @Comment("어드민")
    @JoinColumn(name = "user_category_id")
    private UserCategory userCategory;

    @Comment("어드민")
    private String refreshToken;

    @OneToMany(mappedBy = "admin")
    @Comment("어드민")
    @Builder.Default
    private List<AdminImage> adminImageList = new ArrayList<>();

    // 아마도 OneToOne 조회시 Eager 로 동작 하겠지만 명확성과 통일성을 위해 이렇게 간다.
    @OneToOne(mappedBy = "admin")
    private AdminProfileImage adminProfileImage;

    // 회원 탈퇴일
    // 회원 탈퇴일이 지난 회원은 로그인이 불가능하도록 수정
    // 회원 탈퇴일 3개월 후 회원 정보를 삭제하는 로직을 쿼츠로 구현
    @Comment("회원 탈퇴일")
    @Column(name = "leave_date")
    @Builder.Default
    private LocalDateTime leaveDate = null;

    public void setUserCategory(UserCategory userCategory) {
        if(this.userCategory != null) {
            this.userCategory.getAdminList().remove(this);
        }
        this.userCategory = userCategory;
        userCategory.getAdminList().add(this);
    }

    public void withdrawAdmin(UserCategory userCategory) {
        this.setUserCategory(userCategory);
        this.adminAbout = null;
        this.refreshToken = null;
        this.password = null;
        this.memo = null;
    }

    public void modifyAdmin(ModifyAdminRequestDto modifyAdminRequestDto) {
        if(StringUtils.hasText(modifyAdminRequestDto.getNickname())) { // 닉네임은 변경되지 않았을 때만 변경 공백이나 null 불가능
            this.nickname = modifyAdminRequestDto.getNickname();
        }
        this.memo = modifyAdminRequestDto.getMemo();
    }

    public void modifyMyInfo(ModifyMyInfoRequestDto request) {
        if(StringUtils.hasText(request.getNickname())) { // 닉네임은 변경되지 않았을 때만 변경 공백이나 null 불가능
            this.nickname = request.getNickname();
        }
        this.memo = request.getMemo();
    }

    public void setLeaveDate(LocalDateTime leaveDate) {this.leaveDate = leaveDate;}
    public void setPassword (String password) {
        this.password = password;
    }
    public void setRefreshToken (String refreshToken) {
        this.refreshToken = refreshToken;
    }


}
