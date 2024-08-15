package com.example.ref.config.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    public static List<String> messageList = new ArrayList<>();
    private final ChannelTopic noticeChannelTopic;

    // 유저가 구독
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try{
            log.info("RedisSubscriber onMessage 실행, message : {}", message);
            String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
            log.info("RedisSubscriber onMessage 실행, publishMessage : {}", publishMessage);
            HashMap<?, ?> hashMap = objectMapper.readValue(publishMessage, HashMap.class);
            log.info("Redis Subscriber onMessage 실행, messageDto : {}", hashMap);
            messageList.add(publishMessage);
            log.info("Redis Subscriber onMessage 실행, pattern : {}", new String (pattern));
            // sessionId
            log.info("Redis Subscriber onMessage 실행, sessionId : {}", hashMap.get("session"));

            if(new String(pattern).equals(noticeChannelTopic.getTopic())) {
                simpMessageSendingOperations.convertAndSend("/topic"+noticeChannelTopic.getTopic(), hashMap);
                log.info("Redis Subscriber onMessage 마지막 실행, noticeChannelTopic : {}", "/topic"+noticeChannelTopic.getTopic());
                log.info("Redis Subscriber onMessage 마지막 실행, hashMap : {}", hashMap);
                log.info("Redis Subscriber onMessage 마지막 실행, hashMap.get(\"session\") : {}", hashMap.get("session"));
            }


        } catch (JsonProcessingException e) {
            log.error("RedisSubscriber onMessage error : {}", e.getMessage());
        }
    }


    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        if(sessionId != null) {
            headerAccessor.setSessionId(sessionId);
        }
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

}
