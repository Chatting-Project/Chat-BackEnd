package com.chat.socket.event;

import com.chat.service.dtos.chat.UpdateChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class PublishReadEvent {
    private final Long memberId;
    private final Long chatRoomId;
    private final Long previousLastReadChatId;
    private final Long currentLastReadChatId;
    private final Map<Long, UpdateChatRoom> updatesByMemberId;
}
