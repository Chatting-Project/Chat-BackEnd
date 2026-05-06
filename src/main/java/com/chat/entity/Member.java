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
@Table(name = "member", uniqueConstraints
        = @UniqueConstraint(
                name = "uq_member_username",
                columnNames = "username"
        )
)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private Member(String username, String password, String nickname) {
        validateUsername(username);
        validatePassword(password);
        validateNickname(nickname);

        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public static Member of(String username, String password, String nickname) {
        return new Member(username, password, nickname);
    }

    public void changeNickname(String nickname) {
        validateNickname(nickname);
        this.nickname = nickname;
    }

    public void changePassword(String encodedPassword) {
        validatePassword(encodedPassword);
        this.password = encodedPassword;
    }

    private static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_USERNAME);
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_PASSWORD);
        }
    }

    private static void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_NICKNAME);
        }
    }
}