package com.chat.api.response.discussion;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiscussionResponse {

    private Long discussionId;
    private Long messageId;

    public DiscussionResponse(Long discussionId, Long messageId) {
        this.discussionId = discussionId;
        this.messageId = messageId;
    }
}
