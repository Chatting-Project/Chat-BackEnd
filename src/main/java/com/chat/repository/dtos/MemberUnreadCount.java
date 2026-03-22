package com.chat.repository.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUnreadCount {

    private Long memberId;
    private Long unreadMemberCount;

    public MemberUnreadCount(Long memberId, Long unreadMemberCount) {
        this.memberId = memberId;
        this.unreadMemberCount = unreadMemberCount;
    }
}
