package com.chat.entity;

import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "space")
public class Space extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "space_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    private Space(String title) {
        validateTitle(title);
        this.title = title;
    }

    public static Space of(String title) {
        return new Space(title);
    }

    public void rename(String title) {
        validateTitle(title);
        this.title = title;
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_SPACE_TITLE);
        }
    }
}
