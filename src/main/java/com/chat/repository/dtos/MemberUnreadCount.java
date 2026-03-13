package com.chat.repository.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUnreadCount {

    private Long memberId;
    private Long unreadCount;

    public MemberUnreadCount(Long memberId, Long unreadCount) {
        this.memberId = memberId;
        this.unreadCount = unreadCount;
    }
}
