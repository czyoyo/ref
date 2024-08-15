package com.example.ref.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDataSubscriber implements MessageListener {

    private final SimpMessageSendingOperations template;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 발행된 데이터를 Deserialize하여 ChatMessage로 변환한다.
        log.info("RedisSubscriber onMessage 실행, message : {}", message);
        template.convertAndSend("/sub/data", message.getBody());
    }

}
