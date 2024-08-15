package com.example.ref.util;

import com.example.ref.entity.Admin;
import com.example.ref.entity.User;
import com.example.ref.entity.UserCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthUtils {

    // 토큰에서 유저 인증 정보를 가져옴
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // 유저 인증 정보에서 유저 아이디를 가져옴
    public static String getUserId() {
        return getAuthentication().getName();
    }


    // 유저 인증 정보에서 유저 권한을 가져옴
    public static String getUserRole() {
        return getAuthentication().getAuthorities().toString();
    }

    // 유저 회원 탈퇴 여부 확인
    public static boolean isLeaveUser(User user) {
        return user.getLeaveDate() != null; // 탈퇴일이 존재하면 true
    }

    public static boolean isLeaveAdmin(Admin admin) {
        return admin.getLeaveDate() != null; // 탈퇴일이 존재하면 true
    }

    public static void isExistUser(User user) {
        if(user == null) {
            throw new IllegalArgumentException("존재하지 않는 유저 입니다.");
        }
        if (isLeaveUser(user)) {
            throw new IllegalArgumentException("탈퇴한 유저 입니다.");
        }
    }


    // 어드민 존재 하는지 체크 + 탈퇴 여부 체크
    public static void isExistAdmin(Admin admin) {
        if(admin == null) {
            throw new IllegalArgumentException("존재하지 않는 어드민 입니다.");
        }
        if (isLeaveAdmin(admin)) {
            throw new IllegalArgumentException("탈퇴한 어드민 입니다.");
        }
    }


    public static String getParentRole(Admin admin) {
        if(admin.getUserCategory() == null) {
            return "No User Auth Data";
        }
        UserCategory userCategory = admin.getUserCategory();

        return userCategory.getParent() != null ? userCategory.getParent().getTitle() : "No Data";
    }

    public static Long getParentRoleId(Admin admin) {
        if(admin.getUserCategory() == null) {
            return 0L;
        }
        UserCategory userCategory = admin.getUserCategory();

        return userCategory.getParent() != null ? userCategory.getParent().getId() : null;
    }



    // 어드민을 받아서 권한의 제일 끝에 ROLE_ 을 붙여서 리턴
    public static String getRole(Admin admin) {

        if(admin.getUserCategory() == null) {
            return "No User Auth Data";
        }
        UserCategory userCategory = admin.getUserCategory();

        return userCategory.getTitle() != null ? userCategory.getTitle() : "No Data";

    }






}
