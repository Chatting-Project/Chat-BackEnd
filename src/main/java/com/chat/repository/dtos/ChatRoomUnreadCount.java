package com.chat.repository.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomUnreadCount {

    private Long chatRoomId;
    private Long unreadMessageCount;

    public ChatRoomUnreadCount(Long chatRoomId, Long unreadMessageCount) {
        this.chatRoomId = chatRoomId;
        this.unreadMessageCount = unreadMessageCount;
    }
}
