package com.chat.socket.manager;

import com.chat.exception.CustomException;
import com.chat.utils.consts.SessionConst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
class ChatRoomManagerTest {

    @Autowired
    private ChatRoomManager chatRoomManager;

    @BeforeEach
    void init() {
        chatRoomManager.clearAll();
    }

    @Test
    @DisplayName("채팅방 ID가 유효하지 않으면 예외 발생")
    void invalidChatRoomIdTest() {
        WebSocketSession session = mock(WebSocketSession.class);
        given(session.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, 1L));

        assertThatThrownBy(() -> chatRoomManager.addSessionToRoom(session, null))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("채팅방이 존재하지 않으면 빈 세트를 반환한다")
    void nonExistentRoomTest() {
        Long chatRoomId = 999L;

        Set<WebSocketSession> result = chatRoomManager.getWebSocketSessionBy(chatRoomId);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채팅방이 없으면 false 를 반환하고 예외가 발생하지 않는다.")
    void removeChatRoomSession_noRoomTest() {
        Long chatRoomId = 999L;
        Long memberId = 42L;

        WebSocketSession mockSession = mock(WebSocketSession.class);
        given(mockSession.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        boolean result = chatRoomManager.removeChatRoomSession(chatRoomId, mockSession);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("채팅방 자체가 없으면 isInRoom이 false를 반환한다.")
    void isInRoom_returnsFalseForNonExistentRoom() {
        // given
        Long chatRoomId = 999L;
        Long memberId = 10L;

        // when & then
        assertThat(chatRoomManager.isInRoom(chatRoomId, memberId)).isFalse();
    }

    @Test
    @DisplayName("ENTER_ROOM 하면 isRoomActive가 true를 반환한다.")
    void isRoomActive_returnsTrueWhenActivated() {
        Long chatRoomId = 1L;
        Long memberId = 10L;

        WebSocketSession session = mock(WebSocketSession.class);
        given(session.getId()).willReturn("session-1");
        given(session.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        chatRoomManager.registerSession(session);
        chatRoomManager.addSessionToRoom(session, chatRoomId);

        assertThat(chatRoomManager.isRoomActive(memberId, chatRoomId)).isTrue();
    }

    @Test
    @DisplayName("ENTER_ROOM 후 ROOM_INACTIVE 하면 isRoomActive가 false를 반환한다.")
    void isRoomActive_returnsFalseAfterEnterAndInactivate() {
        Long chatRoomId = 1L;
        Long memberId = 10L;

        WebSocketSession session = mock(WebSocketSession.class);
        given(session.getId()).willReturn("session-1");
        given(session.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        chatRoomManager.registerSession(session);
        chatRoomManager.addSessionToRoom(session, chatRoomId);    // auto-activate 됨
        chatRoomManager.deactivateRoom("session-1", chatRoomId);  // deactivate

        assertThat(chatRoomManager.isRoomActive(memberId, chatRoomId)).isFalse();
    }

    @Test
    @DisplayName("ROOM_INACTIVE 후 ROOM_ACTIVE 하면 isRoomActive가 다시 true를 반환한다.")
    void isRoomActive_returnsTrueAfterReactivate() {
        Long chatRoomId = 1L;
        Long memberId = 10L;

        WebSocketSession session = mock(WebSocketSession.class);
        given(session.getId()).willReturn("session-1");
        given(session.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        chatRoomManager.registerSession(session);
        chatRoomManager.addSessionToRoom(session, chatRoomId);   // auto-activate
        chatRoomManager.deactivateRoom("session-1", chatRoomId); // ROOM_INACTIVE
        chatRoomManager.activateRoom("session-1", chatRoomId);   // ROOM_ACTIVE (복구)

        assertThat(chatRoomManager.isRoomActive(memberId, chatRoomId)).isTrue();
    }

    @Test
    @DisplayName("다중 세션 중 하나라도 active이면 isRoomActive가 true를 반환한다.")
    void isRoomActive_returnsTrueIfAnySessionActive() {
        Long chatRoomId = 1L;
        Long memberId = 10L;

        WebSocketSession sessionA = mock(WebSocketSession.class);
        given(sessionA.getId()).willReturn("session-a");
        given(sessionA.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        WebSocketSession sessionB = mock(WebSocketSession.class);
        given(sessionB.getId()).willReturn("session-b");
        given(sessionB.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        chatRoomManager.registerSession(sessionA);
        chatRoomManager.addSessionToRoom(sessionA, chatRoomId);   // auto-activate A

        chatRoomManager.registerSession(sessionB);
        chatRoomManager.addSessionToRoom(sessionB, chatRoomId);   // auto-activate B
        chatRoomManager.deactivateRoom("session-b", chatRoomId);  // B를 inactive로 전환

        // A만 active인 상태
        assertThat(chatRoomManager.isRoomActive(memberId, chatRoomId)).isTrue();
    }

    @Test
    @DisplayName("다중 세션 모두 inactive이면 isRoomActive가 false를 반환한다.")
    void isRoomActive_returnsFalseWhenAllSessionsInactive() {
        Long chatRoomId = 1L;
        Long memberId = 10L;

        WebSocketSession sessionA = mock(WebSocketSession.class);
        given(sessionA.getId()).willReturn("session-a");
        given(sessionA.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        WebSocketSession sessionB = mock(WebSocketSession.class);
        given(sessionB.getId()).willReturn("session-b");
        given(sessionB.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        // sessionA: add → auto-activate → deactivate
        chatRoomManager.registerSession(sessionA);
        chatRoomManager.addSessionToRoom(sessionA, chatRoomId);
        chatRoomManager.deactivateRoom("session-a", chatRoomId);

        // sessionB: add → auto-activate → deactivate
        chatRoomManager.registerSession(sessionB);
        chatRoomManager.addSessionToRoom(sessionB, chatRoomId);
        chatRoomManager.deactivateRoom("session-b", chatRoomId);

        assertThat(chatRoomManager.isRoomActive(memberId, chatRoomId)).isFalse();
    }

    @Test
    @DisplayName("방 전환(ENTER_ROOM) 시 이전 방의 active 상태가 자동으로 클리어된다.")
    void addSessionToRoom_clearsActiveStateOnRoomSwitch() {
        Long room1 = 1L;
        Long room2 = 2L;
        Long memberId = 10L;

        WebSocketSession session = mock(WebSocketSession.class);
        given(session.getId()).willReturn("session-1");
        given(session.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        chatRoomManager.registerSession(session);
        chatRoomManager.addSessionToRoom(session, room1);   // auto-activate room1
        assertThat(chatRoomManager.isRoomActive(memberId, room1)).isTrue();

        // room2로 이동
        chatRoomManager.addSessionToRoom(session, room2);

        assertThat(chatRoomManager.isRoomActive(memberId, room1)).isFalse();  // room1 deactivated
        assertThat(chatRoomManager.isRoomActive(memberId, room2)).isTrue();   // room2 activated
    }

    @Test
    @DisplayName("removeSessionState 후 isRoomActive가 false를 반환한다.")
    void removeSessionState_clearsActiveState() {
        Long chatRoomId = 1L;
        Long memberId = 10L;

        WebSocketSession session = mock(WebSocketSession.class);
        given(session.getId()).willReturn("session-1");
        given(session.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        chatRoomManager.registerSession(session);
        chatRoomManager.addSessionToRoom(session, chatRoomId);   // auto-activate
        assertThat(chatRoomManager.isRoomActive(memberId, chatRoomId)).isTrue();

        chatRoomManager.removeSessionState(session);

        assertThat(chatRoomManager.isRoomActive(memberId, chatRoomId)).isFalse();
    }

    @Test
    @DisplayName("방에 등록되지 않은 세션으로 activateRoom을 호출해도 active 상태가 되지 않는다.")
    void activateRoom_rejectsSessionNotInRoom() {
        Long chatRoomId = 1L;
        Long memberId = 10L;

        WebSocketSession session = mock(WebSocketSession.class);
        given(session.getId()).willReturn("session-1");
        given(session.getAttributes()).willReturn(Map.of(SessionConst.SESSION_ID, memberId));

        chatRoomManager.registerSession(session);
        // addSessionToRoom 호출 안 함 — 방에 없음

        chatRoomManager.activateRoom("session-1", chatRoomId);

        assertThat(chatRoomManager.isRoomActive(memberId, chatRoomId)).isFalse();
    }
}
