package com.chat.socket.event;

import com.chat.service.dtos.chat.BroadcastChat;
import lombok.Getter;

@Getter
public class PublishMessageEvent {

    private final BroadcastChat broadcastChat;
    private final Long chatRoomId;

    public PublishMessageEvent(BroadcastChat broadcastChat, Long chatRoomId) {
        this.broadcastChat = broadcastChat;
        this.chatRoomId = chatRoomId;
    }
}
