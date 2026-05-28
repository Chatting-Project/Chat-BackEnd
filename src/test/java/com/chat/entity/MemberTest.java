package com.chat.entity;

import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("회원 엔티티를 생성한다.")
    void createMemberTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "nickname";

        // when
        Member member = Member.of(username, password, nickname);

        // then
        assertThat(member.getUsername()).isEqualTo(username);
        assertThat(member.getPassword()).isEqualTo(password);
        assertThat(member.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("닉네임을 변경한다.")
    void changeNicknameTest() {
        // given
        Member member = Member.of("username", "password", "oldNickname");

        // when
        member.changeNickname("newNickname");

        // then
        assertThat(member.getNickname()).isEqualTo("newNickname");
    }

    @Test
    @DisplayName("비밀번호를 변경한다.")
    void changePasswordTest() {
        // given
        Member member = Member.of("username", "oldPassword", "nickname");

        // when
        member.changePassword("newEncodedPassword");

        // then
        assertThat(member.getPassword()).isEqualTo("newEncodedPassword");
    }

    @Test
    @DisplayName("사용자 ID 가 없을 시 회원 엔티티를 생성하면 CustomException 이 발생한다.")
    void emptyUsernameCreateMemberFailTest() {
        // given
        String password = "password";
        String nickname = "nickname";

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> Member.of(null, password, nickname))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_USERNAME);
        });
    }

    @Test
    @DisplayName("사용자 ID 가 공백이면 회원 엔티티를 생성하면 CustomException 이 발생한다.")
    void blankUsernameCreateMemberFailTest() {
        // given
        String username = "  ";
        String password = "password";
        String nickname = "nickname";

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> Member.of(username, password, nickname))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_USERNAME);
        });
    }

    @Test
    @DisplayName("비밀번호가 없을 시 회원 엔티티를 생성하면 CustomException 이 발생한다.")
    void nullPasswordCreateMemberFailTest() {
        // given
        String username = "username";
        String nickname = "nickname";

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> Member.of(username, null, nickname))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_PASSWORD);
        });
    }

    @Test
    @DisplayName("비밀번호가 공백이면 회원 엔티티를 생성하면 CustomException 이 발생한다.")
    void blankPasswordCreateMemberFailTest() {
        // given
        String username = "username";
        String password = "  ";
        String nickname = "nickname";

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> Member.of(username, password, nickname))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_PASSWORD);
        });
    }

    @Test
    @DisplayName("닉네임이 없을 시 회원 엔티티를 생성하면 CustomException 이 발생한다.")
    void nullNicknameCreateMemberFailTest() {
        // given
        String username = "username";
        String password = "password";

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> Member.of(username, password, null))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_NICKNAME);
        });
    }

    @Test
    @DisplayName("닉네임이 공백이면 회원 엔티티를 생성하면 CustomException 이 발생한다.")
    void blankNicknameCreateMemberFailTest() {
        // given
        String username = "username";
        String password = "password";
        String nickname = "  ";

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> Member.of(username, password, nickname))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_NICKNAME);
        });
    }

    @Test
    @DisplayName("null 닉네임으로 변경하면 CustomException 이 발생한다.")
    void changeNicknameNullFailTest() {
        // given
        Member member = Member.of("username", "password", "nickname");

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> member.changeNickname(null))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_NICKNAME);
        });
    }

    @Test
    @DisplayName("공백 닉네임으로 변경하면 CustomException 이 발생한다.")
    void changeNicknameBlankFailTest() {
        // given
        Member member = Member.of("username", "password", "nickname");

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> member.changeNickname("  "))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_NICKNAME);
        });
    }

    @Test
    @DisplayName("null 비밀번호로 변경하면 CustomException 이 발생한다.")
    void changePasswordNullFailTest() {
        // given
        Member member = Member.of("username", "password", "nickname");

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> member.changePassword(null))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_PASSWORD);
        });
    }

    @Test
    @DisplayName("공백 비밀번호로 변경하면 CustomException 이 발생한다.")
    void changePasswordBlankFailTest() {
        // given
        Member member = Member.of("username", "password", "nickname");

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> member.changePassword("  "))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_PASSWORD);
        });
    }
}