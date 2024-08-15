package com.example.ref.config.socket;

import com.example.ref.config.jwt.JwtTokenProvider;
import com.example.ref.rules.SessionType;
import com.example.ref.util.RedisUtils;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)// 최상위로 설정, 다른 인터셉터보다 먼저 실행되도록 설정
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final SessionRegistry sessionRegistry;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisUtils redisUtils;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {


        // 구독 이라면 통과 소켓 연결, 구독 해제, 구독 메시지는 토큰 검사를 하지 않는다.
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 연결 해제, 구독 해제, 구독 메시지는 토큰 검사를 하지 않는다.

        // DISCONNECT: 연결 해제,
        // SUBSCRIBE: 구독
        if (String.valueOf(accessor.getCommand()).equals("DISCONNECT") || String.valueOf(accessor.getCommand()).equals("SUBSCRIBE")) {
            return message;
        }

        // 헤더 토큰 추출
        String authorizationHeader = String.valueOf(Objects.requireNonNull(accessor.getNativeHeader("Authorization")).get(0));

        // null 체크
        if (authorizationHeader == null || authorizationHeader.isEmpty() || authorizationHeader.equals("null")) {
            log.error("authorizationHeader is null or empty value: {} ", authorizationHeader);
            throw new MessageDeliveryException("토큰이 없습니다.");
        }

        // 토큰 인증
        String token = jwtTokenProvider.resolveToken(authorizationHeader);

        // 토큰으로 유저 아이디 추출
        if(String.valueOf(accessor.getCommand()).equals("CONNECT")) {
            String userId = jwtTokenProvider.getAuthenticationNameToSocket(token);
            // 세션 prefix 를 추가 해준다.
//            userId = SessionType.IMPRESSION.getType() + userId;
//            sessionRegistry.registerNewSession(userId, accessor.getSessionId());

            // 세션 을 레디스에 저장
            redisUtils.setValueWithPrefixAndTimeUnit(
                userId,
                accessor.getSessionId(),
                SessionType.SESSION_ID.getType(),
                24,
                TimeUnit.HOURS
            );

            log.info("연결 성공");
            // 세션 아이디
            log.info("세션 아이디 : {}", accessor.getSessionId());
        }

        jwtTokenProvider.validateSocketToken(token);

        return message;
    }



}
