package com.example.ref.entity;

import com.example.ref.request.admin.category.user.ModifyUserCategoryRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "user_category")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("인덱스")
    private long id;

    @Column(name = "title", columnDefinition = "VARCHAR(255)", unique = true, nullable = false)
    @Comment("권한명")
    private String title;

    @Column(name="classification", columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("분류")
    private String classification;

    @Column(name = "memo", columnDefinition = "TEXT")
    @Comment("설명")
    private String memo;

    @JoinColumn(name = "parent_id")
    @Comment("같은 테이블의 부모 인덱스")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserCategory parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<UserCategory> children = new ArrayList<>();

    @Column(name = "depth", columnDefinition = "INT UNSIGNED", nullable = false)
    @Comment("뎁스")
    private int depth;

    @OneToMany(mappedBy = "userCategory", fetch = FetchType.LAZY)
    @Comment("카테고리에 속한 어드민")
    private List<Admin> adminList = new ArrayList<>();

    @OneToMany(mappedBy = "userCategory", fetch = FetchType.LAZY)
    @Comment("카테고리에 속한 유저")
    private List<User> userList = new ArrayList<>();


    public void modifyUserCategory(ModifyUserCategoryRequestDto modifyUserCategoryRequestDto) {
        this.memo = modifyUserCategoryRequestDto.getMemo();
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setParent(UserCategory parentUserCategory) {
        if(this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        this.parent = parentUserCategory;
    }


}
