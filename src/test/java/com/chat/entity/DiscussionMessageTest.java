package com.chat.entity;

import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiscussionMessageTest {

    @Test
    @DisplayName("DiscussionMessage 엔티티를 생성한다.")
    void createDiscussionMessageTest() {
        // given
        Member member = Member.of("username", "password", "nickname");
        Space space = Space.of("개발팀");
        Message rootMessage = Message.of("안녕하세요", member, space);
        Discussion discussion = Discussion.of(rootMessage);
        String content = "답글입니다";

        // when
        DiscussionMessage discussionMessage = DiscussionMessage.of(content, discussion, member);

        // then
        assertThat(discussionMessage.getContent()).isEqualTo(content);
        assertThat(discussionMessage.getDiscussion()).isEqualTo(discussion);
        assertThat(discussionMessage.getMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("content 가 없을 시 DiscussionMessage 엔티티를 생성하면 CustomException 이 발생한다.")
    void nullContentCreateDiscussionMessageFailTest() {
        // given
        Member member = Member.of("username", "password", "nickname");
        Discussion discussion = createDiscussion(member);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> DiscussionMessage.of(null, discussion, member))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_DISCUSSION_MESSAGE_CONTENT);
        });
    }

    @Test
    @DisplayName("content 가 공백이면 DiscussionMessage 엔티티를 생성하면 CustomException 이 발생한다.")
    void blankContentCreateDiscussionMessageFailTest() {
        // given
        Member member = Member.of("username", "password", "nickname");
        Discussion discussion = createDiscussion(member);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> DiscussionMessage.of("  ", discussion, member))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_DISCUSSION_MESSAGE_CONTENT);
        });
    }

    @Test
    @DisplayName("discussion 이 없을 시 DiscussionMessage 엔티티를 생성하면 CustomException 이 발생한다.")
    void nullDiscussionCreateDiscussionMessageFailTest() {
        // given
        Member member = Member.of("username", "password", "nickname");

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> DiscussionMessage.of("답글입니다", null, member))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.DISCUSSION_NOT_FOUND);
        });
    }

    @Test
    @DisplayName("member 가 없을 시 DiscussionMessage 엔티티를 생성하면 CustomException 이 발생한다.")
    void nullMemberCreateDiscussionMessageFailTest() {
        // given
        Member member = Member.of("username", "password", "nickname");
        Discussion discussion = createDiscussion(member);

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> DiscussionMessage.of("답글입니다", discussion, null))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        });
    }

    private Discussion createDiscussion(Member member) {
        Space space = Space.of("개발팀");
        Message rootMessage = Message.of("안녕하세요", member, space);
        return Discussion.of(rootMessage);
    }
}
