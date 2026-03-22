package com.chat.service.dtos;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatHistory {

    private Long chatId;
    private Long senderId;
    private String senderNickname;
    private String message;
    private Long unreadMemberCount;
    private LocalDateTime createdDate;

    @Builder
    public ChatHistory(Long chatId, Long senderId, String senderNickname, String message, Long unreadMemberCount, LocalDateTime createdDate) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.message = message;
        this.unreadMemberCount = unreadMemberCount;
        this.createdDate = createdDate;
    }
}
