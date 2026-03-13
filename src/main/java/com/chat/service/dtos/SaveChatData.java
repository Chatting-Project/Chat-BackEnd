package com.chat.service.dtos;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SaveChatData {

    private Long chatId;
    private Long unReadCount;
    private LocalDateTime createdDate;

    @Builder
    public SaveChatData(Long chatId, Long unReadCount, LocalDateTime createdDate) {
        this.chatId = chatId;
        this.unReadCount = unReadCount;
        this.createdDate = createdDate;
    }
}
