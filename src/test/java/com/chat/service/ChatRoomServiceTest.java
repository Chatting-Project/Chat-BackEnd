package com.chat.service;

import com.chat.api.response.chatroom.ChatRoomsResponse;
import com.chat.api.response.chatroom.OpponentResponse;
import com.chat.entity.Chat;
import com.chat.entity.ChatRead;
import com.chat.entity.ChatRoom;
import com.chat.entity.ChatRoomParticipant;
import com.chat.entity.Member;
import com.chat.fixture.TestDataFixture;
import com.chat.repository.ChatReadRepository;
import com.chat.repository.ChatRepository;
import com.chat.repository.ChatRoomParticipantRepository;
import com.chat.repository.ChatRoomRepository;
import com.chat.service.dtos.SaveChatRoomDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomParticipantRepository chatRoomParticipantRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatReadRepository chatReadRepository;
    @Autowired
    private TestDataFixture fixture;

    @Test
    @DisplayName("채팅방을 저장한다.")
    void saveChatRoomTest() {
        // given
        String title = "title";

        Member sender = fixture.savedMemberBy("sender");
        Member firstReceiver = fixture.savedMemberBy("firstReceiver");
        Member secondReceiver = fixture.savedMemberBy("secondReceiver");

        Set<Long> receiverIds = new HashSet<>();
        receiverIds.add(firstReceiver.getId());
        receiverIds.add(secondReceiver.getId());

        SaveChatRoomDTO dto = SaveChatRoomDTO
                .builder()
                .title(title)
                .senderId(sender.getId())
                .receiverIds(receiverIds)
                .build();

        // when
        Long savedChatRoomId = chatRoomService.saveChatRoom(dto);

        // then
        ChatRoom chatRoom = chatRoomRepository.findById(savedChatRoomId).get();

        List<ChatRoomParticipant> chatRoomParticipants
                = chatRoomParticipantRepository.findAllFetchMemberBy(savedChatRoomId);

        Set<Long> participantMemberIds = chatRoomParticipants.stream()
                .map(p -> p.getMember().getId())
                .collect(Collectors.toSet());

        Set<Long> expectedMemberIds = Set.of(
                sender.getId(),
                firstReceiver.getId(),
                secondReceiver.getId()
        );

        assertThat(chatRoom.getTitle()).isEqualTo(title);
        assertThat(participantMemberIds)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(expectedMemberIds);
    }

    @Test
    @DisplayName("채팅방 목록을 조회한다.")
    void findChatRoomsTest() {
        // given
        Member first = fixture.savedMemberBy("first");
        Member second = fixture.savedMemberBy("second");
        Member third = fixture.savedMemberBy("third");
        Member fourth = fixture.savedMemberBy("fourth");

        Long firstId = first.getId();
        List<Member> secondParticipants = createParticipantsBy(first, second);
        fixture.savedChatRoomBy("title", secondParticipants);

        List<Member> thirdParticipants = createParticipantsBy(first, third);
        fixture.savedChatRoomBy("title", thirdParticipants);

        List<Member> fourthParticipants = createParticipantsBy(first, fourth);
        fixture.savedChatRoomBy("title", fourthParticipants);

        // when
        List<ChatRoomsResponse> chatRooms = chatRoomService.findChatRooms(first.getId());

        // then
        assertThat(chatRooms).hasSize(3);
    }

    @Test
    @DisplayName("메시지가 없는 채팅방 조회 시 lastMessage 는 null, unReadCount 는 0 이다.")
    void findChatRooms_withNoMessagesTest() {
        // given
        Member me = fixture.savedMemberBy("me");
        Member other = fixture.savedMemberBy("other");
        fixture.savedChatRoomBy("title", List.of(me, other));

        // when
        List<ChatRoomsResponse> chatRooms = chatRoomService.findChatRooms(me.getId());

        // then
        assertThat(chatRooms).hasSize(1);
        assertThat(chatRooms.get(0).getLastMessage()).isNull();
        assertThat(chatRooms.get(0).getUnreadMessageCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("채팅방 목록 조회 시 가장 마지막 메시지 정보가 포함된다.")
    void findChatRooms_lastMessageTest() {
        // given
        Member me = fixture.savedMemberBy("me");
        Member other = fixture.savedMemberBy("other");
        ChatRoom chatRoom = fixture.savedChatRoomBy("title", List.of(me, other));

        fixture.savedSimpleChat("first message", other, chatRoom);
        fixture.savedSimpleChat("last message", other, chatRoom);

        // when
        List<ChatRoomsResponse> chatRooms = chatRoomService.findChatRooms(me.getId());

        // then
        assertThat(chatRooms).hasSize(1);
        assertThat(chatRooms.get(0).getLastMessage()).isEqualTo("last message");
        assertThat(chatRooms.get(0).getCreatedDate()).isNotNull();
    }

    @Test
    @DisplayName("채팅방 목록 조회 시 읽지 않은 메시지 수가 포함된다.")
    void findChatRooms_unReadCountTest() {
        // given
        Member me = fixture.savedMemberBy("me");
        Member other = fixture.savedMemberBy("other");
        ChatRoom chatRoom = fixture.savedChatRoomBy("title", List.of(me, other));

        Chat firstChat = fixture.savedSimpleChat("msg1", other, chatRoom);
        Chat secondChat = fixture.savedSimpleChat("msg2", other, chatRoom);

        chatReadRepository.save(new ChatRead(false, me, firstChat));
        chatReadRepository.save(new ChatRead(false, me, secondChat));

        // when
        List<ChatRoomsResponse> chatRooms = chatRoomService.findChatRooms(me.getId());

        // then
        assertThat(chatRooms).hasSize(1);
        assertThat(chatRooms.get(0).getUnreadMessageCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("채팅방 목록 조회 시 본인을 제외한 상대방 정보가 포함된다.")
    void findChatRooms_opponentsTest() {
        // given
        Member me = fixture.savedMemberBy("me");
        Member other = fixture.savedMemberBy("other");
        fixture.savedChatRoomBy("title", List.of(me, other));

        // when
        List<ChatRoomsResponse> chatRooms = chatRoomService.findChatRooms(me.getId());

        // then
        assertThat(chatRooms).hasSize(1);
        List<OpponentResponse> opponents = chatRooms.get(0).getOpponents();
        assertThat(opponents).hasSize(1);
        assertThat(opponents.get(0).getOpponentId()).isEqualTo(other.getId());
    }

    // todo connect & broadCastMessage 테스트 필요

    private List<Member> createParticipantsBy(Member first, Member second) {
        List<Member> participants = new ArrayList<>();
        participants.add(first);
        participants.add(second);

        return participants;
    }
}