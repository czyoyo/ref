package com.example.ref.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class CustomReTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if(request.getHeader("Authorization") == null || !request.getHeader("Authorization").startsWith("Bearer ")){
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
            return;
        }

        // accessToken 을 헤더에서 꺼내옴
        String accessToken = request.getHeader("Authorization");
        // 토큰 Bearer 제거
        accessToken = jwtTokenProvider.resolveToken(accessToken);
        // Access Token 을 이용하여 Authentication 객체를 만듬
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        // SecurityContext 에 Authentication 객체를 저장 (이게 있어야 SecurityContextHolder 에서 꺼내 쓸 수 있음) (이게 없으면 SecurityContextHolder.getContext().getAuthentication() 이 null 이 됨)
        // SecurityContextHolder.getContext().getAuthentication() 이 null 이 되면 SecurityContextHolder.getContext().setAuthentication(authentication); 이 부분에서
        // authentication 이 null 이 되어서 NullPointerException 이 발생함
        // SecurityContextHolder 는 현재 쓰레드의 보안 정보를 담는 저장소
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);


        String authoritiesString = String.join(",", authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toArray(String[]::new)
        );

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.setHeader("roles", authoritiesString);

        filterChain.doFilter(request, response);

    }
}
