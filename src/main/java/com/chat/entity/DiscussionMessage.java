package com.chat.entity;

import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "discussion_message")
public class DiscussionMessage extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "discussion_message_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private DiscussionMessage(String content, Discussion discussion, Member member) {
        validateContent(content);
        validateDiscussion(discussion);
        validateMember(member);

        this.content = content;
        this.discussion = discussion;
        this.member = member;
    }

    public static DiscussionMessage of(
            String content,
            Discussion discussion,
            Member member
    ) {
        return new DiscussionMessage(content, discussion, member);
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_DISCUSSION_MESSAGE_CONTENT);
        }
    }

    private static void validateDiscussion(Discussion discussion) {
        if (discussion == null) {
            throw new CustomException(ErrorCode.DISCUSSION_NOT_FOUND);
        }
    }

    private static void validateMember(Member member) {
        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }
}