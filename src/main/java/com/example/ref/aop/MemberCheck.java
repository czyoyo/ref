package com.example.ref.aop;

import com.example.ref.entity.Admin;
import com.example.ref.entity.User;
import com.example.ref.repository.AdminRepository;
import com.example.ref.repository.UserRepository;
import com.example.ref.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MemberCheck {


    private final AdminRepository adminRepository;


    // 어드민은 탈퇴 사용자인지 확인
    @Around("execution(* com.example.ref.service.admin.*.*(..)) && !@annotation(com.example.ref.annotation.NoAuthCheck)")
    public Object adminLeaveCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        String userId = AuthUtils.getUserId();
        Admin admin = adminRepository.findByAdminId(userId);

        AuthUtils.isExistAdmin(admin);

        // 원본 메소드 실행
        return joinPoint.proceed();

    }


}
