package com.chat.socket.event;

import com.chat.service.dtos.chat.UpdateChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class PublishUpdateEvent {

    private final Long chatRoomId;
    private final Map<Long, UpdateChatRoom> updatesByMemberId;
}
