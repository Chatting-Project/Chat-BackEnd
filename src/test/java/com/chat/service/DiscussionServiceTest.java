package com.chat.service;

import com.chat.api.response.discussion.DiscussionResponse;
import com.chat.entity.Discussion;
import com.chat.entity.Member;
import com.chat.entity.Message;
import com.chat.entity.Space;
import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import com.chat.fixture.TestDataFixture;
import com.chat.repository.DiscussionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class DiscussionServiceTest {

    @Autowired private DiscussionService discussionService;
    @Autowired private DiscussionRepository discussionRepository;
    @Autowired private TestDataFixture fixture;

    @Test
    @DisplayName("Message 기반으로 Discussion을 생성한다.")
    void Message_기반으로_Discussion을_생성한다() {
        // given
        Member member = fixture.savedMemberBy("member");
        Space space = fixture.savedChatRoomBy("space", List.of(member));
        Message message = fixture.savedSimpleChat("content", member, space);

        // when
        DiscussionResponse response = discussionService.createDiscussion(message.getId(), member.getId());

        // then
        assertThat(response.getDiscussionId()).isNotNull();
        assertThat(response.getMessageId()).isEqualTo(message.getId());
    }

    @Test
    @DisplayName("동일 Message에 Discussion이 이미 존재하면 생성이 거부된다.")
    void 동일_Message에_Discussion이_이미_존재하면_생성이_거부된다() {
        // given
        Member member = fixture.savedMemberBy("member");
        Space space = fixture.savedChatRoomBy("space", List.of(member));
        Message message = fixture.savedSimpleChat("content", member, space);
        discussionRepository.save(Discussion.of(message));

        // when / then
        assertThatThrownBy(() -> discussionService.createDiscussion(message.getId(), member.getId()))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.DISCUSSION_ALREADY_EXISTS));
    }

    @Test
    @DisplayName("Space 참여자가 아닌 member가 Discussion 생성을 시도하면 접근이 거부된다.")
    void Space_미참여자가_Discussion_생성을_시도하면_접근이_거부된다() {
        // given
        Member participant = fixture.savedMemberBy("participant");
        Member outsider = fixture.savedMemberBy("outsider");
        Space space = fixture.savedChatRoomBy("space", List.of(participant));
        Message message = fixture.savedSimpleChat("content", participant, space);

        // when / then
        assertThatThrownBy(() -> discussionService.createDiscussion(message.getId(), outsider.getId()))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.SPACE_ACCESS_DENIED));
    }

    @Test
    @DisplayName("Message 기반으로 생성된 Discussion을 조회한다.")
    void Message_기반으로_생성된_Discussion을_조회한다() {
        // given
        Member member = fixture.savedMemberBy("member");
        Space space = fixture.savedChatRoomBy("space", List.of(member));
        Message message = fixture.savedSimpleChat("content", member, space);
        Discussion discussion = discussionRepository.save(Discussion.of(message));

        // when
        DiscussionResponse response = discussionService.findDiscussionByMessageId(message.getId(), member.getId());

        // then
        assertThat(response.getDiscussionId()).isEqualTo(discussion.getId());
        assertThat(response.getMessageId()).isEqualTo(message.getId());
    }

    @Test
    @DisplayName("Space 참여자가 아닌 member가 Discussion 조회를 시도하면 접근이 거부된다.")
    void Space_미참여자가_Discussion_조회를_시도하면_접근이_거부된다() {
        // given
        Member participant = fixture.savedMemberBy("participant");
        Member outsider = fixture.savedMemberBy("outsider");
        Space space = fixture.savedChatRoomBy("space", List.of(participant));
        Message message = fixture.savedSimpleChat("content", participant, space);
        discussionRepository.save(Discussion.of(message));

        // when / then
        assertThatThrownBy(() -> discussionService.findDiscussionByMessageId(message.getId(), outsider.getId()))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.SPACE_ACCESS_DENIED));
    }
}
