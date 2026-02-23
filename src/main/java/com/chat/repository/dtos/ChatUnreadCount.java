package com.chat.repository.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatUnreadCount {

    private Long chatId;
    private Long unreadCount;

    public ChatUnreadCount(Long chatId, Long unreadCount) {
        this.chatId = chatId;
        this.unreadCount = unreadCount;
    }
}
