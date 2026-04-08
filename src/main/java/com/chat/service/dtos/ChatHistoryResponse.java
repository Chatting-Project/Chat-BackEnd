package com.chat.service.dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatHistoryResponse {

    private Long lastReadChatId;
    private List<ChatHistory> messages;
    private boolean hasMore;

    public ChatHistoryResponse(Long lastReadChatId, List<ChatHistory> messages, boolean hasMore) {
        this.lastReadChatId = lastReadChatId;
        this.messages = messages;
        this.hasMore = hasMore;
    }
}
