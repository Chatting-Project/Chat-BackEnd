package com.chat.socket.manager;

import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import com.chat.service.dtos.chat.EnterChatRoom;
import com.chat.utils.consts.SessionConst;
import com.chat.utils.valid.IdValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatRoomManager {

    private final Map<Long, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> memberToRoomsMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public void addSessionToRoom(WebSocketSession session, Long chatRoomId) {

        IdValidator.requireChatRoomId(chatRoomId);
        Long loginMemberId = (Long) session.getAttributes().get(SessionConst.SESSION_ID);

        chatRooms.computeIfAbsent(chatRoomId, key -> ConcurrentHashMap.newKeySet()).add(session);
        memberToRoomsMap.computeIfAbsent(loginMemberId, k -> ConcurrentHashMap.newKeySet()).add(chatRoomId);
    }

    public void broadcastEnterChatRoom(Long chatRoomId, EnterChatRoom enterChatRoom) {

        IdValidator.requireChatRoomId(chatRoomId);

        try {
            String enterChatRoomMessage = objectMapper.writeValueAsString(enterChatRoom);
            Set<WebSocketSession> sessions = chatRooms.get(chatRoomId);

            for (WebSocketSession session : sessions) {
                session.sendMessage(new TextMessage(enterChatRoomMessage));
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.CHAT_ROOM_BROADCAST_IO_EXCEPTION);
        }
    }

    public Set<WebSocketSession> getWebSocketSessionBy(Long chatRoomId) {
        Set<WebSocketSession> sessions = chatRooms.get(chatRoomId);
        if (sessions == null || sessions.isEmpty()) {
            throw new CustomException(ErrorCode.WEB_SOCKET_SESSION_NOT_EXIST);
        }
        return sessions;
    }

    public Set<Long> getChatRoomIdsBy(Long memberId) {
        return memberToRoomsMap.get(memberId);
    }

    public void removeChatRoomSession(Long chatRoomId, Long memberId) {
        Set<WebSocketSession> sessions = chatRooms.get(chatRoomId);
        if (sessions == null) {
            return;
        }

        List<WebSocketSession> toRemove = new ArrayList<>();
        for (WebSocketSession session : sessions) {
            Object userIdObj = session.getAttributes().get(SessionConst.SESSION_ID);
            if (userIdObj instanceof Long && ((Long) userIdObj).equals(memberId)) {
                toRemove.add(session);
            }
        }

        sessions.removeAll(toRemove);
    }
}
