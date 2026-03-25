package com.chat.service.dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatHistoryResponse {

    private Long lastReadChatId;
    private List<ChatHistory> messages;

    public ChatHistoryResponse(Long lastReadChatId, List<ChatHistory> messages) {
        this.lastReadChatId = lastReadChatId;
        this.messages = messages;
    }
}
