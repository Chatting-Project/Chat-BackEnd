package com.chat.socket.listener;

import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import com.chat.service.dtos.chat.ReadEvent;
import com.chat.service.dtos.chat.UpdateChatRoom;
import com.chat.socket.event.PublishMessageEvent;
import com.chat.socket.event.PublishReadEvent;
import com.chat.socket.event.PublishUpdateEvent;
import com.chat.socket.manager.ChatRoomManager;
import com.chat.socket.manager.WebsocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatBroadcastListener {

    private final ChatRoomManager chatRoomManager;
    private final WebsocketSessionManager websocketSessionManager;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishMessageToSessions(PublishMessageEvent event) {
        sendToRoomSessions(event.getChatRoomId(), event.getBroadcastChat());
        sendUpdateChatRoom(event.getUpdatesByMemberId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishReadEventToSessions(PublishReadEvent event) {
        ReadEvent readEvent = new ReadEvent(event.getMemberId(), event.getChatRoomId(), event.getLastReadChatId());
        sendToRoomSessions(event.getChatRoomId(), readEvent);
        sendUpdateChatRoom(event.getUpdatesByMemberId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishUpdateEventToSessions(PublishUpdateEvent event) {
        sendUpdateChatRoom(event.getUpdatesByMemberId());
    }

    private void sendToRoomSessions(Long chatRoomId, Object payload) {
        send(chatRoomManager.getWebSocketSessionBy(chatRoomId), payload);
    }

    private void sendUpdateChatRoom(Map<Long, UpdateChatRoom> updatesByMemberId) {
        updatesByMemberId
                .forEach((memberId, updateChatRoom) -> {
                    send(websocketSessionManager.getSessionBy(memberId), updateChatRoom);
                });
    }

    private void send(Collection<WebSocketSession> sessions, Object payload) {
        if (sessions.isEmpty()) {
            return;
        }

        String message;
        try {
            message = objectMapper.writeValueAsString(payload);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.CHAT_ROOM_BROADCAST_IO_EXCEPTION);
        }

        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                continue;
            }
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.warn("전송 실패 : session={}", session.getId(), e);
            }
        }
    }
}
