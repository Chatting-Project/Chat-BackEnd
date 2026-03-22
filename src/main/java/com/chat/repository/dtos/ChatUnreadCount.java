package com.chat.repository.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatUnreadCount {

    private Long chatId;
    private Long unreadMemberCount;

    public ChatUnreadCount(Long chatId, Long unreadMemberCount) {
        this.chatId = chatId;
        this.unreadMemberCount = unreadMemberCount;
    }
}
