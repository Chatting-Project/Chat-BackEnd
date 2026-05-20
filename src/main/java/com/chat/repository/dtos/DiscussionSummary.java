package com.chat.repository.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiscussionSummary {

    private Long messageId;
    private Long discussionId;
    private Long discussionMessageCount;

    public DiscussionSummary(Long messageId, Long discussionId, Long discussionMessageCount) {
        this.messageId = messageId;
        this.discussionId = discussionId;
        this.discussionMessageCount = discussionMessageCount;
    }
}
