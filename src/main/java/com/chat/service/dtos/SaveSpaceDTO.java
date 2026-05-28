package com.chat.service.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveSpaceDTO {

    private Long senderId;
    private String title;

    @Builder
    public SaveSpaceDTO(Long senderId, String title) {
        this.senderId = senderId;
        this.title = title;
    }
}
