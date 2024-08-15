package com.example.ref.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class SingleVisitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;
    public static final String REDIS_KEY = "ip:";
    public static final String USER_AGENT = "User-Agent";
    public static final String TODAY = "today:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String ip = getIp(request);
        String userAgent = request.getHeader(USER_AGENT);
        // T 로 자르기
        String today = LocalDate.now().toString().split("T")[0];
        String key = REDIS_KEY+ip+"|"+TODAY+today;

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        if(Boolean.FALSE.equals(valueOperations.getOperations().hasKey(key))){
            valueOperations.set(key, userAgent == null ? "unknown" : userAgent);
        }

        return true;
    }

    private String getIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
    return ip;

}

}
