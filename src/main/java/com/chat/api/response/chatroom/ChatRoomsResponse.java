package com.chat.api.response.chatroom;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ChatRoomsResponse {

    private Long chatRoomId;
    private String title;
    private String lastMessage;
    private Long unReadCount;
    private LocalDateTime createdDate;
    private List<OpponentResponse> opponents;

    @Builder
    public ChatRoomsResponse(Long chatRoomId, String title, String lastMessage, Long unReadCount, LocalDateTime createdDate, List<OpponentResponse> opponents) {
        this.chatRoomId = chatRoomId;
        this.title = title;
        this.lastMessage = lastMessage;
        this.unReadCount = unReadCount;
        this.createdDate = createdDate;
        this.opponents = opponents;
    }
}
