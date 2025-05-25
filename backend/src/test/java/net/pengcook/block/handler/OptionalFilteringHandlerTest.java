package net.pengcook.block.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import java.util.Set;
import net.pengcook.block.domain.BlackList;
import net.pengcook.block.domain.Ownable;
import net.pengcook.block.test.TestOwnable;
import net.pengcook.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OptionalFilteringHandlerTest {

    private final OptionalFilteringHandler handler = new OptionalFilteringHandler();

    @Test
    @DisplayName("Optional 타입을 지원한다")
    void canHandleWhenWithOptional() {
        Optional<Ownable> optional = Optional.of(new TestOwnable(1L));

        boolean canHandle = handler.canHandle(optional);

        assertThat(canHandle).isTrue();
    }

    @Test
    @DisplayName("Optional이 아닌 타입은 지원하지 않는다")
    void canHandleWhenNonOptional() {
        assertAll(
                () -> assertThat(handler.canHandle("not optional")).isFalse(),
                () -> assertThat(handler.canHandle(123)).isFalse(),
                () -> assertThat(handler.canHandle(null)).isFalse()
        );
    }

    @Test
    @DisplayName("빈 Optional은 그대로 반환한다")
    void handleFilteringWhenEmptyOptional() {
        Optional<?> emptyOptional = Optional.empty();
        BlackList blackList = new BlackList(Set.of());

        Object result = handler.handleFiltering(emptyOptional, blackList);

        assertThat(result).isSameAs(emptyOptional);
    }

    @Test
    @DisplayName("Ownable이 아닌 값이 담긴 Optional은 그대로 반환한다")
    void handleFilteringWhenNonOwnableOptional() {
        Optional<String> notOwnableOptional = Optional.of("test");
        User blockedUser = User.builder()
                .id(1L)
                .build();
        BlackList blackList = new BlackList(Set.of(blockedUser));

        Object result = handler.handleFiltering(notOwnableOptional, blackList);

        assertThat(result).isSameAs(notOwnableOptional);
    }

    @Test
    @DisplayName("차단된 Ownable이 담긴 Optional은 빈 Optional로 반환한다")
    void handleFilteringWhenBlockedOwnable() {
        Optional<Ownable> optional = Optional.of(new TestOwnable(1L));
        User blockedUser = User.builder()
                .id(1L)
                .build();
        BlackList blackList = new BlackList(Set.of(blockedUser));

        Object result = handler.handleFiltering(optional, blackList);

        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("차단되지 않은 Ownable이 담긴 Optional은 원본 Optional로 반환한다")
    void handleFilteringWhenAllowedOwnable() {
        Optional<Ownable> optional = Optional.of(new TestOwnable(2L));
        User blockedUser = User.builder()
                .id(1L)
                .build();
        BlackList blackList = new BlackList(Set.of(blockedUser));

        Object result = handler.handleFiltering(optional, blackList);

        assertThat(result).isSameAs(optional);
    }
}
