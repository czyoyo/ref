package com.example.ref.config.jwt;

import com.example.ref.rules.RedisType;
import com.example.ref.rules.ResponseCode;
import com.example.ref.util.RedisUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Slf4j
public class JwtTokenProvider implements InitializingBean {

    private final Logger logger = Logger.getLogger(String.valueOf(JwtTokenProvider.class));
    private final String secretKey;
    private final String AUTHORITIES_KEY;
    private final Long accessTokenValidityInMilliseconds; // 액세스 토큰 유효시간
    private final Long refreshTokenValidityInMilliseconds; // 리프레시 토큰 유효시간
    private Key key; // 토큰을 만들 때 사용
    private final RedisUtils redisUtils;



    public JwtTokenProvider(
        @Value("${jwt.secret}") String secretKey,
        @Value("${jwt.access-token-validity-in-seconds}") Long accessTokenValidityInMilliseconds,
        @Value("${jwt.roles}") String authoritiesKey,
        @Value("${jwt.refresh-token-validity-in-seconds}") Long refreshTokenValidityInMilliseconds,
        RedisUtils redisUtils
    ) {
        this.secretKey = secretKey;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.AUTHORITIES_KEY = authoritiesKey;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
        this.redisUtils = redisUtils;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = secretKey.getBytes(); // 키를 바이트로 바꿈
        this.key = Keys.hmacShaKeyFor(keyBytes); // 키 생성
    }

    private HttpServletRequest getCurrentHttpRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }



    public String createAccessToken(Authentication authentication) {

        long now = (new Date()).getTime();
        Date expirationTime = new Date(now + accessTokenValidityInMilliseconds); // 토큰 만료 시간

        // 권한 정보를 문자열로 가져옴
        Claims claims = generateClaims(authentication, expirationTime);
        return generateToken(claims);
    }

    private Claims generateClaims(Authentication authentication, Date expirationTime) {
        Claims claims = Jwts.claims();
        claims.put(AUTHORITIES_KEY, authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
        claims.setExpiration(expirationTime);
        claims.setIssuedAt(new Date(System.currentTimeMillis()));
        claims.setSubject(authentication.getName());
        return claims;
    }

    private String generateToken(Claims claims) {
        return Jwts.builder()
            .setClaims(claims)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    public String createRefreshToken(Authentication authentication) {

        long now = (new Date()).getTime();
        Date expirationTime = new Date(now + refreshTokenValidityInMilliseconds); // 토큰 만료 시간

        // 권한 정보를 문자열로 가져옴
        Claims claims = generateClaims(authentication, expirationTime);
        String compact = generateToken(claims);

        // redis 에 저장
        redisUtils.setValueWithPrefixAndTimeUnit(authentication.getName(), compact, RedisType.REFRESH_TOKEN.getType(), Math.toIntExact(refreshTokenValidityInMilliseconds),
            TimeUnit.MILLISECONDS);

        return compact;
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 문자열을 반환
        }
        return null;
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);// 토큰 파싱
            return true;
        } catch (ExpiredJwtException e) {
            // CustomAuthenticationEntryPoint 에서 exception 을 받아서 처리
            log.error("토큰이 만료되었습니다. : {}", e.getMessage());
            HttpServletRequest request = getCurrentHttpRequest();
            request.setAttribute("exception", ResponseCode.EXPIRED_TOKEN.getCode());
            return false;
        } catch (Exception e) {
            // 나머지는 CustomAuthenticationEntryPoint 에서 401 에러로 처리
            log.error("토큰이 유효하지 않습니다. : {}, {}", e.getMessage());
            return false;
        }
    }

    public void validateSocketToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);// 토큰 파싱
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // StompErrorHandler 에서 exception 을 받아서 처리
            log.error("잘못된 JWT 서명입니다. : {}", e.getMessage());
            throw new MalformedJwtException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            // StompErrorHandler 에서 exception 을 받아서 처리
            log.error("토큰이 만료되었습니다. : {}", e.getMessage());
            throw new ExpiredJwtException(null, null, "토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            // StompErrorHandler 에서 exception 을 받아서 처리
            log.error("지원되지 않는 JWT 토큰입니다. : {}", e.getMessage());
            throw new UnsupportedJwtException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            // StompErrorHandler 에서 exception 을 받아서 처리
            log.error("JWT 토큰이 잘못되었습니다. : {}", e.getMessage());
            throw new IllegalArgumentException("JWT 토큰이 잘못되었습니다.");
        }
    }




    public Authentication getAuthentication(String token) {

        HttpServletRequest request = getCurrentHttpRequest();

        try {
            // 토큰으로부터 유저 정보를 받아옴
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                .getBody();
            String userId = claims.getSubject();
            // 권한 정보를 가져옴
            Collection<? extends GrantedAuthority> authorities = getSimpleGrantedAuthorities(token);
            User user = new User(userId, "", authorities);
            return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        } catch (ExpiredJwtException e) {
            // CustomAuthenticationEntryPoint 에서 exception 을 받아서 처리
            log.info("토큰이 만료되었습니다. : {}", e.getMessage());
            request.setAttribute("exception", ResponseCode.EXPIRED_TOKEN.getCode());
            return null;
        } catch (Exception e) {
            log.info("토큰이 유효하지 않습니다. : {}", e.getMessage());
            return null;
        }
    }

    public Date getExpiredDate(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                .getBody().getExpiration();
        } catch (ExpiredJwtException e) {
            // CustomAuthenticationEntryPoint 에서 exception 을 받아서 처리
            log.info("토큰이 만료되었습니다. : {}", e.getMessage());
            HttpServletRequest request = getCurrentHttpRequest();
            request.setAttribute("exception", ResponseCode.EXPIRED_TOKEN.getCode());
            return null;
        } catch (Exception e) {
            log.info("토큰이 유효하지 않습니다. : {}", e.getMessage());
            return null;
        }
    }


    public String getAuthenticationName(String token) {

        HttpServletRequest request = getCurrentHttpRequest();

        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
                .getSubject();
        } catch (ExpiredJwtException e) {
            // CustomAuthenticationEntryPoint 에서 exception 을 받아서 처리
            log.info("토큰이 만료되었습니다. : {}", e.getMessage());
            request.setAttribute("exception", ResponseCode.EXPIRED_TOKEN.getCode());
            return null;
        } catch (Exception e) {
            log.info("토큰이 유효하지 않습니다. : {}", e.getMessage());
            return null;
        }
    }

    public String getAuthenticationNameToSocket(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
                .getSubject();
        } catch (ExpiredJwtException e) {
            // CustomAuthenticationEntryPoint 에서 exception 을 받아서 처리
            log.info("토큰이 만료되었습니다. : {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.info("토큰이 유효하지 않습니다. : {}", e.getMessage());
            return null;
        }
    }

    public Collection<SimpleGrantedAuthority> getSimpleGrantedAuthorities(String token) {
        try {
            return Arrays.stream(
                    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
                        .get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        } catch (ExpiredJwtException e) {
            // CustomAuthenticationEntryPoint 에서 exception 을 받아서 처리
            log.info("토큰이 만료되었습니다. : {}", e.getMessage());
            HttpServletRequest request = getCurrentHttpRequest();
            request.setAttribute("exception", ResponseCode.EXPIRED_TOKEN.getCode());
            return null;
        } catch (Exception e) {
            log.info("토큰이 유효하지 않습니다. : {}", e.getMessage());
            return null;
        }
    }














}
