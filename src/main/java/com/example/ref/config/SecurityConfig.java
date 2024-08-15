package com.example.ref.config;

import com.example.ref.config.jwt.CustomReTokenFilter;
import com.example.ref.config.jwt.JwtFilter;
import com.example.ref.config.jwt.JwtSecurityConfig;
import com.example.ref.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;
import org.springframework.http.HttpMethod;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CorsFilter corsFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {



        http
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new CustomReTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            )
            .authorizeHttpRequests((authorize) -> authorize

                    // 개발단계 허용
                    .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()

                    // 게스트 로그인
                    .requestMatchers(new AntPathRequestMatcher("/api/user/guest/login")).permitAll()

                    // 소켓 path 접근 허용
                    .requestMatchers(new AntPathRequestMatcher("/ws")).permitAll()

                    .requestMatchers(new AntPathRequestMatcher("/pubsub/**")).permitAll()

                    .requestMatchers(new AntPathRequestMatcher("/api/admin/login")).permitAll()

                    .requestMatchers(new AntPathRequestMatcher("/api/user/login")).permitAll()

                    .requestMatchers(new AntPathRequestMatcher("/api/admin/refresh")).permitAll()

                    .requestMatchers(new AntPathRequestMatcher("/api/user/refresh")).permitAll()

                    // 게스트의 경우 특정 Path 들은 허용 해 줘야 한다. get 만 허용

                    // 유저 상세 정보 가져오기 게스트 허용
                    .requestMatchers(new AntPathRequestMatcher("/api/user", HttpMethod.GET.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/user/{id}", HttpMethod.GET.name())).permitAll()
                    // 어드민 정보 가져오기 게스트 허용
                    .requestMatchers(new AntPathRequestMatcher("/api/admin/**", HttpMethod.GET.name())).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/admin/{id}", HttpMethod.GET.name())).permitAll()

                    // 관리자 페이지 검색
                    .requestMatchers(new AntPathRequestMatcher("/api/admin/redis/get-my-search", HttpMethod.GET.name())).hasAnyRole("ADMIN", "ADMIN_GUEST")
                    .requestMatchers(new AntPathRequestMatcher("/api/admin/redis/add-my-search")).hasAnyRole("ADMIN")

                    // 엑셀
                    .requestMatchers(new AntPathRequestMatcher("/api/admin/excel/**", HttpMethod.GET.name())).hasAnyRole("ADMIN", "ADMIN_GUEST")
                    .requestMatchers(new AntPathRequestMatcher("/api/admin/excel/**")).hasAnyRole("ADMIN")

                    // 관리자 권한
                    .requestMatchers(new AntPathRequestMatcher("/api/admin/**", HttpMethod.GET.name())).hasAnyRole("ADMIN", "ADMIN_GUEST")
                    .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasAnyRole("ADMIN")

                    // 유저 권한
                    .requestMatchers(new AntPathRequestMatcher("/api/user/**")).hasAnyRole("USER", "USER_GUEST")

                    .anyRequest().authenticated()

            )
            .headers(headers -> headers
                .frameOptions(FrameOptionsConfig::sameOrigin
                )
            )
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }




}
