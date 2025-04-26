package net.pengcook.block.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Set;
import net.pengcook.block.domain.BlackList;
import net.pengcook.block.test.TestOwnable;
import net.pengcook.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class OwnableFilteringHandlerTest {

    private final OwnableFilteringHandler handler = new OwnableFilteringHandler();

    @Test
    @DisplayName("Ownable 타입을 지원한다.")
    void canHandle() {
        TestOwnable ownable = new TestOwnable(1L);

        boolean canHandle = handler.canHandle(ownable);

        assertThat(canHandle).isTrue();
    }

    @Test
    @DisplayName("Ownable 타입이 아니면 지원하지 않는다.")
    void canHandleWhenNotOwnable() {
        assertAll(
                () -> assertThat(handler.canHandle("not Ownable")).isFalse(),
                () -> assertThat(handler.canHandle(123)).isFalse()
        );
    }

    @Test
    @DisplayName("차단한 사용자의 컨텐츠는 필터링 한다.")
    void handleFiltering() {
        TestOwnable ownable = new TestOwnable(1L);
        User blockedUser = User.builder().id(1L).build();
        BlackList blackList = new BlackList(Set.of(blockedUser));

        Object actual = handler.handleFiltering(ownable, blackList);

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("차단하지 않은 사용자의 컨텐츠는 필터링하지 않는다.")
    void handleFilteringNotBlocked() {
        TestOwnable ownable = new TestOwnable(2L);
        User blockedUser = User.builder()
                .id(1L)
                .build();
        BlackList blackList = new BlackList(Set.of(blockedUser));

        Object actual = handler.handleFiltering(ownable, blackList);

        assertThat(actual).isEqualTo(new TestOwnable(2L));
    }
}
