package com.chat.socket.manager;

import com.chat.entity.Chat;
import com.chat.repository.ChatReadRepository;
import com.chat.repository.ChatRepository;
import com.chat.repository.MemberRepository;
import com.chat.service.dtos.chat.UpdateChatRoom;
import com.chat.utils.message.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebsocketSessionManager {

    // 소켓에 연결된 사용자 정보
    private final Map<Long, WebSocketSession> activeMemberSessions = new ConcurrentHashMap<>();
    private final ChatRoomManager chatRoomManager;
    private final ChatRepository chatRepository;
    private final ChatReadRepository chatReadRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    public void addSession(Long memberId, WebSocketSession session) {
        activeMemberSessions.put(memberId, session);
    }

    public WebSocketSession getSessionBy(Long memberId) {
        return activeMemberSessions.get(memberId);
    }

    public void removeSession(Long memberId) {
        activeMemberSessions.remove(memberId);

        chatRoomManager.removeChatRoomsSessionBy(memberId);
    }

    public void broadcastToChatRoomMembers(Long chatRoomId) throws IOException {
        List<Long> memberIdsInChatRoom = memberRepository.findMemberIdsIn(chatRoomId);

        Pageable limitOne = createLimitOne();

        for (Long memberId : memberIdsInChatRoom) {
            WebSocketSession session = activeMemberSessions.get(memberId);

            if (session == null) {
                return;
            }

            Chat lastChat = chatRepository
                    .findLastChatBy(chatRoomId, limitOne)
                    .stream()
                    .findFirst()
                    .orElse(null);
            Long unReadCount = chatReadRepository.findUnReadCountBy(chatRoomId, memberId);

            UpdateChatRoom updateChatRoom = UpdateChatRoom
                    .builder()
                    .messageType(MessageType.UPDATE_CHAT_ROOM)
                    .chatRoomId(chatRoomId)
                    .lastMessage(lastChat != null ? lastChat.getMessage() : null)
                    .createdDate(lastChat != null ? lastChat.getCreatedDate() : null)
                    .unReadCount(unReadCount)
                    .build();
            String updateChatRoomString = objectMapper.writeValueAsString(updateChatRoom);

            session.sendMessage(new TextMessage(updateChatRoomString));
        }
    }

    private Pageable createLimitOne() {
        return PageRequest.of(0, 1);
    }
}
