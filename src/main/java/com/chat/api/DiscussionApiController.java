package com.chat.api;

import com.chat.api.response.discussion.DiscussionResponse;
import com.chat.service.DiscussionService;
import com.chat.utils.consts.SessionConst;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
public class DiscussionApiController {

    private final DiscussionService discussionService;

    @GetMapping("/api/messages/{messageId}/discussion")
    public Result<DiscussionResponse> findDiscussion(@PathVariable Long messageId,
                                                     @SessionAttribute(name = SessionConst.SESSION_ID) Long loginMemberId) {

        DiscussionResponse response = discussionService.findDiscussionByMessageId(messageId, loginMemberId);

        return Result.<DiscussionResponse>builder()
                .data(response)
                .status(HttpStatus.OK)
                .message("Discussion 조회에 성공했습니다.")
                .build();
    }

    @PostMapping("/api/messages/{messageId}/discussion")
    public Result<DiscussionResponse> createDiscussion(@PathVariable Long messageId,
                                                       @SessionAttribute(name = SessionConst.SESSION_ID) Long loginMemberId) {

        DiscussionResponse response = discussionService.createDiscussion(messageId, loginMemberId);

        return Result.<DiscussionResponse>builder()
                .data(response)
                .status(HttpStatus.CREATED)
                .message("Discussion 생성에 성공했습니다.")
                .build();
    }
}
