package com.chat.service;

import com.chat.api.response.discussion.DiscussionResponse;
import com.chat.entity.Discussion;
import com.chat.entity.Message;
import com.chat.entity.SpaceMember;
import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import com.chat.repository.DiscussionRepository;
import com.chat.repository.MessageRepository;
import com.chat.repository.SpaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscussionService {

    private final MessageRepository messageRepository;
    private final DiscussionRepository discussionRepository;
    private final SpaceMemberRepository spaceMemberRepository;

    public DiscussionResponse findDiscussionByMessageId(Long messageId, Long memberId) {
        Message message = findAccessibleMessage(messageId, memberId);

        Discussion discussion = discussionRepository.findByRootMessageId(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.DISCUSSION_NOT_FOUND));

        return new DiscussionResponse(discussion.getId(), message.getId());
    }

    @Transactional
    public DiscussionResponse createDiscussion(Long messageId, Long memberId) {
        Message message = findAccessibleMessage(messageId, memberId);

        if (discussionRepository.existsByRootMessageId(messageId)) {
            throw new CustomException(ErrorCode.DISCUSSION_ALREADY_EXISTS);
        }

        // TODO: DB UNIQUE(root_message_id) 동시 충돌 시 DataIntegrityViolationException 처리 보강 필요
        Discussion saved = discussionRepository.save(Discussion.of(message));

        return new DiscussionResponse(saved.getId(), message.getId());
    }

    private Message findAccessibleMessage(Long messageId, Long memberId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));

        Long spaceId = message.getSpace().getId();
        SpaceMember spaceMember = spaceMemberRepository.findChatRoomBy(spaceId, memberId);
        if (spaceMember == null) {
            throw new CustomException(ErrorCode.SPACE_ACCESS_DENIED);
        }

        return message;
    }
}
