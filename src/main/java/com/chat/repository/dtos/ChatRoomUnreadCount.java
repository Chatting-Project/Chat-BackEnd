package com.chat.repository.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomUnreadCount {

    private Long chatRoomId;
    private Long unreadCount;

    public ChatRoomUnreadCount(Long chatRoomId, Long unreadCount) {
        this.chatRoomId = chatRoomId;
        this.unreadCount = unreadCount;
    }
}
