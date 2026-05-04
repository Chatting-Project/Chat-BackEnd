package com.chat.api.request.chatroom;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class SaveSpaceRequest {

    private Set<Long> receiverIds;
    private String title;

    public SaveSpaceRequest(Set<Long> receiverIds, String title) {
        this.receiverIds = receiverIds;
        this.title = title;
    }
}
