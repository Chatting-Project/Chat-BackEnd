package com.chat.service;

import com.chat.entity.Chat;
import com.chat.entity.ChatRead;
import com.chat.entity.ChatRoom;
import com.chat.entity.Member;
import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import com.chat.fixture.TestDataFixture;
import com.chat.repository.ChatReadRepository;
import com.chat.service.dtos.chat.UpdateChatRoom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BroadcastDataBuilderTest {

    @Autowired
    private BroadcastDataBuilder broadcastDataBuilder;
    @Autowired
    private ChatReadRepository chatReadRepository;
    @Autowired
    private TestDataFixture fixture;
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("채팅방에 참여 중인 멤버가 없으면 빈 Map을 반환한다.")
    void build_noMembers_returnsEmptyMap() {
        // given
        ChatRoom chatRoom = fixture.savedSimpleChatRoom("title");

        // when
        Map<Long, UpdateChatRoom> result = broadcastDataBuilder.build(chatRoom.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("chatRoomId에 해당하는 채팅방이 존재하지 않으면 CustomException이 발생한다.")
    void build_chatRoomNotFound_throwsException() {
        // given
        Member me = fixture.savedMemberBy("me");
        ChatRoom chatRoom = fixture.savedChatRoomBy("title", List.of(me));
        Long chatRoomId = chatRoom.getId();

        // FK 제약 임시 해제 후 chatRoom만 삭제 (데이터 정합성이 깨진 상태 재현)
        em.flush();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        em.createNativeQuery("DELETE FROM chat_room WHERE chat_room_id = :id")
                .setParameter("id", chatRoomId)
                .executeUpdate();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        em.clear();

        // when & then
        assertThatThrownBy(() -> broadcastDataBuilder.build(chatRoomId))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode())
                        .isEqualTo(ErrorCode.CHAT_ROOM_NOT_EXIST));
    }

    @Test
    @DisplayName("채팅 메시지가 없는 방은 lastMessage와 createdDate가 null이다.")
    void build_noMessages_returnsNullLastMessageAndCreatedDate() {
        // given
        Member me = fixture.savedMemberBy("me");
        Member other = fixture.savedMemberBy("other");
        ChatRoom chatRoom = fixture.savedChatRoomBy("title", List.of(me, other));

        // when
        Map<Long, UpdateChatRoom> result = broadcastDataBuilder.build(chatRoom.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(me.getId()).getLastMessage()).isNull();
        assertThat(result.get(me.getId()).getCreatedDate()).isNull();
    }

    @Test
    @DisplayName("멤버별 읽지 않은 메시지 수가 정확히 담긴다.")
    void build_returnsCorrectUnreadCountPerMember() {
        // given
        Member me = fixture.savedMemberBy("me");
        Member other = fixture.savedMemberBy("other");
        ChatRoom chatRoom = fixture.savedChatRoomBy("title", List.of(me, other));

        Chat first = fixture.savedSimpleChat("msg1", other, chatRoom);
        Chat second = fixture.savedSimpleChat("msg2", other, chatRoom);

        chatReadRepository.save(new ChatRead(false, me, first));
        chatReadRepository.save(new ChatRead(false, me, second));
        chatReadRepository.save(new ChatRead(false, other, first));

        // when
        Map<Long, UpdateChatRoom> result = broadcastDataBuilder.build(chatRoom.getId());

        // then
        assertThat(result.get(me.getId()).getUnreadMessageCount()).isEqualTo(2L);
        assertThat(result.get(other.getId()).getUnreadMessageCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("채팅방 title과 마지막 메시지가 정확히 담긴다.")
    void build_returnsTitleAndLastMessage() {
        // given
        Member me = fixture.savedMemberBy("me");
        Member other = fixture.savedMemberBy("other");
        ChatRoom chatRoom = fixture.savedChatRoomBy("myTitle", List.of(me, other));

        fixture.savedSimpleChat("first message", other, chatRoom);
        fixture.savedSimpleChat("last message", other, chatRoom);

        // when
        Map<Long, UpdateChatRoom> result = broadcastDataBuilder.build(chatRoom.getId());

        // then
        UpdateChatRoom updateChatRoom = result.get(me.getId());
        assertThat(updateChatRoom.getTitle()).isEqualTo("myTitle");
        assertThat(updateChatRoom.getLastMessage()).isEqualTo("last message");
        assertThat(updateChatRoom.getCreatedDate()).isNotNull();
    }
}
