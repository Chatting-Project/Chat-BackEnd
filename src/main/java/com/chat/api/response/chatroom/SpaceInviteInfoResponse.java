package com.chat.api.response.chatroom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpaceInviteInfoResponse {

    private Long spaceId;
    private String title;
    private long memberCount;
    private boolean alreadyJoined;
}
