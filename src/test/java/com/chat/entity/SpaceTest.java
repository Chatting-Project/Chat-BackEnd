package com.chat.entity;

import com.chat.exception.CustomException;
import com.chat.exception.ErrorCode;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpaceTest {

    @Test
    @DisplayName("Space 엔티티를 생성한다.")
    void createSpaceTest() {
        // given
        String title = "개발팀";

        // when
        Space space = Space.of(title);

        // then
        assertThat(space.getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("Space 이름을 변경한다.")
    void renameTest() {
        // given
        Space space = Space.of("구 이름");

        // when
        space.rename("신 이름");

        // then
        assertThat(space.getTitle()).isEqualTo("신 이름");
    }

    @Test
    @DisplayName("title 이 없을 시 Space 엔티티를 생성하면 CustomException 이 발생한다.")
    void nullTitleCreateSpaceFailTest() {
        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> Space.of(null))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_SPACE_TITLE);
        });
    }

    @Test
    @DisplayName("title 이 공백이면 Space 엔티티를 생성하면 CustomException 이 발생한다.")
    void blankTitleCreateSpaceFailTest() {
        // given
        String title = "  ";

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> Space.of(title))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_SPACE_TITLE);
        });
    }

    @Test
    @DisplayName("null title 로 변경하면 CustomException 이 발생한다.")
    void renameNullFailTest() {
        // given
        Space space = Space.of("개발팀");

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> space.rename(null))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_SPACE_TITLE);
        });
    }

    @Test
    @DisplayName("공백 title 로 변경하면 CustomException 이 발생한다.")
    void renameBlankFailTest() {
        // given
        Space space = Space.of("개발팀");

        // when
        AbstractObjectAssert<?, CustomException> extracting = assertThatThrownBy(
                () -> space.rename("  "))
                .isInstanceOf(CustomException.class)
                .extracting(ex -> (CustomException) ex);

        // then
        extracting.satisfies(ex -> {
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_SPACE_TITLE);
        });
    }

    @Test
    @DisplayName("Space 생성 시 inviteCode가 자동으로 생성된다.")
    void createSpace_inviteCodeGeneratedTest() {
        // given
        String title = "개발팀";

        // when
        Space space = Space.of(title);

        // then
        assertThat(space.getInviteCode()).isNotNull();
        assertThat(space.getInviteCode()).isNotBlank();
        assertThat(space.getInviteCode()).hasSize(32);
    }

    @Test
    @DisplayName("두 Space의 inviteCode는 서로 다르다.")
    void createSpace_inviteCodeIsUniqueTest() {
        // given & when
        Space space1 = Space.of("팀A");
        Space space2 = Space.of("팀B");

        // then
        assertThat(space1.getInviteCode()).isNotEqualTo(space2.getInviteCode());
    }
}
