package com.chat.api.request.chatroom;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveSpaceRequest {

    private String title;

    public SaveSpaceRequest(String title) {
        this.title = title;
    }
}
