package com.chat.socket.event;

import com.chat.service.dtos.chat.EnterChatRoom;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class PublishEnterRoomEvent {

    private final WebSocketSession session;
    private final Long chatRoomId;
    private final EnterChatRoom enterChatRoom;

    public PublishEnterRoomEvent(WebSocketSession session, Long chatRoomId, EnterChatRoom enterChatRoom) {
        this.session = session;
        this.chatRoomId = chatRoomId;
        this.enterChatRoom = enterChatRoom;
    }
}
