package com.example.ref.service.admin;

import com.example.ref.repository.SearchHistoryRepository;
import com.example.ref.rules.RedisType;
import com.example.ref.util.AuthUtils;
import com.example.ref.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisUtils redisUtils;
    private final SearchHistoryRepository searchHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public String getMyRefreshToken() {
        // 토큰으로 부터 인증 객체를 가져온다.
        String userId = AuthUtils.getUserId();

        // 내 토큰만 가져오기
        return redisUtils.getValuesWithPrefix(userId, RedisType.REFRESH_TOKEN.getType());
    }

}
