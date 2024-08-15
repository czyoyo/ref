package com.example.ref.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public void setValueWithPrefixAndTimeUnit(String key, String value, String prefix, int timeout, TimeUnit timeUnit) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(prefix + key, value, timeout, timeUnit);
    }

    public void addValueWithPrefixAndTimeUnit(String key, String value, String prefix, int timeout, TimeUnit timeUnit) {
        redisTemplate.opsForSet().add(prefix + key, value);
        redisTemplate.expire(prefix + key, timeout, timeUnit);
    }

    public Set<String> getAllKeyAndValues() {

        HashMap<String, Object> map = new HashMap<>();

        // null check
        Set<String> keys = redisTemplate.keys("*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptySet();
        }

        keys.forEach(key -> {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            map.put(key, valueOperations.get(key));
        });


        if(map.isEmpty()) {
            return Collections.emptySet();
        } else {
            return map.keySet();
        }
    }

    @Transactional(readOnly = true)
    public String getValuesWithPrefix(String key, String prefix) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        if(Boolean.TRUE.equals(redisTemplate.hasKey(prefix + key))) {
            return (String) valueOperations.get(prefix + key);
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Set<Object> getSetValue(String key, String prefix) {
        if(Boolean.TRUE.equals(redisTemplate.hasKey(prefix + key))) {
            return redisTemplate.opsForSet().members(prefix + key);
        } else {
            return Collections.emptySet();
        }
    }

    @Transactional(readOnly = true)
    public String getExpireWithPrefix(String key, String prefix) {
        if(Boolean.TRUE.equals(redisTemplate.hasKey(prefix + key))) {
            return String.valueOf(redisTemplate.getExpire(prefix + key));
        } else {
            return null;
        }
    }

    public void deleteSingleSetValue(String key, String value, String prefix) {
        redisTemplate.opsForSet().remove(prefix + key, value);
    }

}
