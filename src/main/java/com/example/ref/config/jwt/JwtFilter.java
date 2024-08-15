package com.example.ref.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@WebFilter
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.header}")
    private String AUTHORIZATION_HEADER = "Authorization";

    public JwtFilter (JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override // OncePerRequestFilter 를 상속받았기 때문에 doFIlter 메서드가 아닌 doFilterInternal 을 구현해야함
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if(request.getHeader(AUTHORIZATION_HEADER) == null || !request.getHeader(AUTHORIZATION_HEADER).startsWith("Bearer")) {
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
            return;
        }

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // 헤더에서 JWT 를 받아옴
        String jwt = jwtTokenProvider.resolveToken(bearerToken);

        // 유효한 토큰인지 확인
        if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt); // 토큰으로부터 유저 정보를 받아옴
            SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext 에 Authentication 객체를 저장
        }
        filterChain.doFilter(request, response);
    }
}
