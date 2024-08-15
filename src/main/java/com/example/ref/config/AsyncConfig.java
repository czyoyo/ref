package com.example.ref.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(10); // 기본 스레드 수
        threadPoolTaskExecutor.setMaxPoolSize(20); // 최대 스레드 수
        threadPoolTaskExecutor.setQueueCapacity(10); // 대기 큐
        threadPoolTaskExecutor.setThreadNamePrefix("async-thread-"); // 스레드 이름 접두사

        // 작업 완료된 후 스레드 풀이 종료될 때까지 대기할 시간 설정(초)
        threadPoolTaskExecutor.setAwaitTerminationSeconds(60); // 대기 시간

        // 스레드 풀의 초기화 여부
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }


}
