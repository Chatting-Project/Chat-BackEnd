package com.chat.service.dtos.chat;

import com.chat.utils.message.MessageType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnterChatRoom{
    private final MessageType messageType;
    private final Long memberId;
    private final Long lastReadChatId;
}
