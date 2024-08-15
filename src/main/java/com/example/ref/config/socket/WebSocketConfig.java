package com.example.ref.config.socket;

import com.example.ref.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;


// Message Broker 에 대한 설정을 한다.
@Configuration
@EnableWebSocketMessageBroker // WebSocket 서버 활성화
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;
    private final StompErrorHandler stompErrorHandler;
    private final JwtTokenProvider jwtTokenProvider;

    // complete
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 브로커
        // topic : 1:N 방식으로 메시지를 발행하고 구독
        // queue : 1:1 방식으로 메시지를 point-to-point 방식으로 보내고 싶을 때 사용
        // registry.enableSimpleBroker("/topic");
        registry.enableSimpleBroker("/topic", "/queue");

        // app 로 데이터를 받으면 이곳을 한번 거쳐서 URI 만 MessageMapping 에 매핑이 된다.
        // ex ) app/chat/message 라면 pub 를 제외하고 /chat/message 를  @MessageMapping("/chat/message") 에 매핑한다.
        // 메지시는 상황에 따라 필요한 경우 바로 브로커로 가는 것이 아니라, 서버에서 가공을 한 후 브로커로 보내야 하는 경우가 있을 수 있다.
        // 메시지 앞에 "/app" 이 붙어있는 경우, 해당 메시지는 @MessageMapping 으로 라우팅 되고, 붙어있지 않은 경우는 브로커로 라우팅 된다.
        registry.setApplicationDestinationPrefixes("/app");
    }


    // HandShake 와 통신을 담당할 Endpoint 를 설정 (클라이언트가 서버에 연결하는데 사용할 웹 소켓 엔드포인트를 등록)
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("SOCKET 연결!");
        registry.addEndpoint("/ws") // ex ) ws://localhost:8080/ws
            .setAllowedOriginPatterns("*")
            .setHandshakeHandler(new DefaultHandshakeHandler());

        registry.setErrorHandler(stompErrorHandler); // 소켓통신 중, 에러가 발생했을 때 처리할 핸들러 등록
    }


    // StompHandler 가 WebSocket 앞단에서 Token 을 검사 하도록 설정
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }




}
