package com.chat.api.response.chatroom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpaceMemberResponse {
    private Long memberId;
    private String nickname;
}
