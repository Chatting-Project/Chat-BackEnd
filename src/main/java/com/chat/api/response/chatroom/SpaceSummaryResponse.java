package com.chat.api.response.chatroom;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SpaceSummaryResponse {

    private Long chatRoomId;
    private String title;
    private String lastMessage;
    private Long lastChatId;
    private Long unreadMessageCount;
    private LocalDateTime createdDate;

    @Builder
    public SpaceSummaryResponse(Long chatRoomId, String title, String lastMessage, Long lastChatId, Long unreadMessageCount, LocalDateTime createdDate) {
        this.chatRoomId = chatRoomId;
        this.title = title;
        this.lastMessage = lastMessage;
        this.lastChatId = lastChatId;
        this.unreadMessageCount = unreadMessageCount;
        this.createdDate = createdDate;
    }
}
