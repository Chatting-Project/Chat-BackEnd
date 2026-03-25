package com.chat.service;

import com.chat.entity.Chat;
import com.chat.entity.ChatRoom;
import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import com.chat.repository.ChatReadRepository;
import com.chat.repository.ChatRepository;
import com.chat.repository.ChatRoomRepository;
import com.chat.repository.MemberRepository;
import com.chat.repository.dtos.MemberUnreadCount;
import com.chat.service.dtos.chat.UpdateChatRoom;
import com.chat.utils.message.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BroadcastDataBuilder {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ChatReadRepository chatReadRepository;

    public Map<Long, UpdateChatRoom> build(Long chatRoomId) {

        if (chatRoomId == null) {
            return Map.of();
        }

        List<Long> memberIds = memberRepository.findMemberIdsIn(chatRoomId);
        if (memberIds.isEmpty()) {
            return Map.of();
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_EXIST)
        );

        Chat lastChat = chatRepository
                .findLastChatBy(chatRoomId, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        Map<Long, Long> unreadCountMap = chatReadRepository
                .findUnReadCountsBy(chatRoomId, memberIds)
                .stream()
                .collect(Collectors.toMap(
                        MemberUnreadCount::getMemberId,
                        MemberUnreadCount::getUnreadMemberCount
                ));

        Map<Long, UpdateChatRoom> result = new HashMap<>();
        for (Long memberId : memberIds) {
            UpdateChatRoom updateChatRoom = UpdateChatRoom.builder()
                    .messageType(MessageType.UPDATE_CHAT_ROOM)
                    .chatRoomId(chatRoomId)
                    .title(chatRoom.getTitle())
                    .lastMessage(lastChat != null ? lastChat.getMessage() : null)
                    .createdDate(lastChat != null ? lastChat.getCreatedDate() : null)
                    .unreadMessageCount(unreadCountMap.getOrDefault(memberId, 0L))
                    .build();
            result.put(memberId, updateChatRoom);
        }

        return result;
    }
}
