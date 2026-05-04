package com.chat.api.response.chatroom;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SpaceResponse {

    private Long chatRoomId;

    public SpaceResponse(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
