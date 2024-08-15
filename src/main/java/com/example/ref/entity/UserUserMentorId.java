package com.example.ref.entity;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

@EqualsAndHashCode
@Comment("유저와 어드민을 묶어주는 테이블의 복합 키")
public class UserUserMentorId implements Serializable {
    @Comment("유저")
    private User user;

    @Comment("어드민")
    private Admin admin;
}
