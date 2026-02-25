package com.chat.socket.event;

import com.chat.service.dtos.chat.EnterChatRoom;
import lombok.Getter;

@Getter
public class PublishEnterRoomEvent {

    private final Long chatRoomId;
    private final EnterChatRoom enterChatRoom;

    public PublishEnterRoomEvent(Long chatRoomId, EnterChatRoom enterChatRoom) {
        this.chatRoomId = chatRoomId;
        this.enterChatRoom = enterChatRoom;
    }
}
