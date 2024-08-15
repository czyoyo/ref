package com.example.ref.config.socket;

import io.jsonwebtoken.ExpiredJwtException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    public StompErrorHandler() {
        super();
    }

    @Override // 클라이언트가 보낸 메시지를 처리하는 도중 에러가 발생했을 때 호출
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage,
        Throwable ex) {

        final Throwable exception = converterThrowException(ex);

        if (exception instanceof ExpiredJwtException) {
            return handleUnauthorizedException(clientMessage, exception);
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }



    private Throwable converterThrowException(final Throwable ex) {
        if (ex instanceof MessageDeliveryException) {
            return ex.getCause();
        }
        return ex;
    }

    private Message<byte[]> handleUnauthorizedException(Message<byte[]> clientMessage, Throwable ex) {
        return prepareErrorMessage(clientMessage, ex.getMessage(), HttpStatus.UNAUTHORIZED.name());
    }


    private Message<byte[]> prepareErrorMessage(final Message<byte[]> clientMessage,
        final String message, final String errorCode) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(errorCode);
        accessor.setLeaveMutable(true);

        setReceiptIdForClientMessage(clientMessage, accessor);

        return MessageBuilder.createMessage(
            message != null ? message.getBytes(StandardCharsets.UTF_8) : EMPTY_PAYLOAD,
            accessor.getMessageHeaders()
        );
    }



    private void setReceiptIdForClientMessage(Message<byte[]> clientMessage,
        StompHeaderAccessor accessor) {

        // 클라이언트가 보낸 메시지에 receipt-id 가 있으면, 에러 메시지에도 receipt-id 를 추가한다.
        if (Objects.isNull(clientMessage)) {
            return;
        }

        // 클라이언트가 보낸 메시지의 헤더를 가져온다.
        final StompHeaderAccessor clientHeaderAccessor = MessageHeaderAccessor.getAccessor(
            clientMessage, StompHeaderAccessor.class);

        // 클라이언트가 보낸 메시지의 헤더에 receipt-id 가 있으면, 에러 메시지의 헤더에도 receipt-id 를 추가한다.
        final String receiptId =
            Objects.isNull(clientHeaderAccessor) ? null : clientHeaderAccessor.getReceipt();

        // receipt-id 가 있으면, 에러 메시지의 헤더에 receipt-id 를 추가한다.
        if (receiptId != null) {
            accessor.setReceiptId(receiptId);

        }
    }

    // 클라이언트가 보낸 메시지를 처리하는 도중 에러가 발생했을 때 호출
    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor,
        byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {

        return MessageBuilder.createMessage(errorPayload, errorHeaderAccessor.getMessageHeaders());
    }
}
