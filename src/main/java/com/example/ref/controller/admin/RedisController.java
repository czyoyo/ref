package com.example.ref.controller.admin;

import com.example.ref.response.CommonResponse;
import com.example.ref.rules.ResponseCode;
import com.example.ref.service.admin.RedisService;
import com.example.ref.util.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/admin/redis")
@Slf4j
public class RedisController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisUtils redisUtils;
    private final RedisService redisService;


//    // 접속 기록 조회
//    @GetMapping(path = "/get-visit-record")
//    @Operation(summary = "접속 기록 조회", description = "접속 기록을 조회합니다.")
//    public String getVisitRecord() {
//        return "GOOD";
//    }
//
//    // 모든 키 조회
//    @GetMapping(path = "/get-all-key")
//    @Operation(summary = "모든 키 조회", description = "모든 키를 조회합니다.")
//    public Set<String> getAllKey() {
//        return redisUtils.getAllKeyAndValues();
//    }


//    @GetMapping(path = "/clear-redis")
//    @Operation(summary = "만료시간 없는 키 삭제", description = "만료시간 없는 키를 삭제합니다.")
//    public String clearNotHavingExpire() {
//
//        // 레디스로 부터 모든 키를 가져온다.
//        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").build();
//        Cursor<String> cursor = redisTemplate.scan(scanOptions);
//
//        while (cursor.hasNext()) {
//            String key = cursor.next();
//            Long ttl = redisTemplate.getExpire(key);
//            // ttl -1 이면 만료시간 없는 것이므로 삭제
//            if (ttl != null && ttl == -1) {
//                redisTemplate.delete(key);
//            }
//        }
//
//        cursor.close();
//
//        return "GOOD CLEAR";
//    }

//    @GetMapping(path = "/all-clear-redis")
//    @Operation(summary = "모든 키 삭제", description = "모든 키를 삭제합니다. ")
//    public String allClearRedis() {
//
//        // 레디스로 부터 모든 키를 가져온다.
//        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").build();
//        Cursor<String> cursor = redisTemplate.scan(scanOptions);
//
//        while (cursor.hasNext()) {
//            String key = cursor.next();
//            redisTemplate.delete(key);
//        }
//
//        cursor.close();
//
//        return "GOOD CLEAR";
//    }


    @GetMapping(path = "/get-my-refresh-token")
    @Operation(summary = "내 리프레시 토큰 조회", description = "내 리프레시 토큰을 조회합니다.")
    public CommonResponse<Object> getMyRefreshToken() {

        String myRefreshToken = redisService.getMyRefreshToken();

        return CommonResponse.builder()
            .data(myRefreshToken)
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .build();
    }








}
