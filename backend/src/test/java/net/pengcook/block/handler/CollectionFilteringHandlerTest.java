package net.pengcook.block.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.pengcook.block.domain.BlackList;
import net.pengcook.block.domain.Ownable;
import net.pengcook.block.test.TestOwnable;
import net.pengcook.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CollectionFilteringHandlerTest {

    private final CollectionFilteringHandler handler = new CollectionFilteringHandler();

    @Test
    @DisplayName("Collection 타입을 지원한다")
    void canHandle() {
        List<TestOwnable> list = new ArrayList<>(List.of(new TestOwnable(1L)));
        Set<TestOwnable> set = new HashSet<>(Set.of(new TestOwnable(1L)));

        assertAll(
                () -> assertThat(handler.canHandle(list)).isTrue(),
                () -> assertThat(handler.canHandle(set)).isTrue()
        );
    }

    @Test
    @DisplayName("Collection이 아닌 타입은 지원하지 않는다")
    void canHandleWhenNonCollection() {
        assertAll(
                () -> assertThat(handler.canHandle("not collection")).isFalse(),
                () -> assertThat(handler.canHandle(123)).isFalse(),
                () -> assertThat(handler.canHandle(null)).isFalse()
        );
    }

    @Test
    @DisplayName("차단된 Ownable이 포함된 List는 필터링 한다")
    void handleFilteringWithListWhenBlocked() {
        List<Ownable> list = new ArrayList<>(List.of(new TestOwnable(1L), new TestOwnable(2L)));
        User blockedUser = User.builder()
                .id(1L)
                .build();
        BlackList blackList = new BlackList(Set.of(blockedUser));

        Object result = handler.handleFiltering(list, blackList);

        assertAll(
                () -> assertThat(result).isSameAs(list),
                () -> assertThat(list).containsExactly(new TestOwnable(2L))
        );
    }

    @Test
    @DisplayName("차단되지 않은 Ownable만 포함된 List는 필터링하지 않는다")
    void handleFilteringWithListWhenNotBlocked() {
        List<Object> list = new ArrayList<>(List.of(new TestOwnable(1L), new TestOwnable(2L)));
        User blockedUser = User.builder()
                .id(3L)
                .build();
        BlackList blackList = new BlackList(Set.of(blockedUser));

        Object result = handler.handleFiltering(list, blackList);

        assertAll(
                () -> assertThat(result).isSameAs(list),
                () -> assertThat(list).containsExactly(new TestOwnable(1L), new TestOwnable(2L))
        );
    }

    @Test
    @DisplayName("차단된 Ownable이 포함된 Set은 필터링 한다")
    void handleFilteringWithSetWhenBlocked() {
        Set<Object> set = new HashSet<>(Set.of(new TestOwnable(1L), new TestOwnable(2L)));
        User blockedUser = User.builder()
                .id(1L)
                .build();
        BlackList blackList = new BlackList(Set.of(blockedUser));

        Object result = handler.handleFiltering(set, blackList);

        assertAll(
                () -> assertThat(result).isSameAs(set),
                () -> assertThat(set).containsExactly(new TestOwnable(2L))
        );
    }

    @Test
    @DisplayName("차단되지 않은 Ownable만 포함된 Set은 필터링하지 않는다")
    void handleFilteringWithSetWhenNotBlocked() {
        Set<Object> set = new HashSet<>(Set.of(new TestOwnable(1L), new TestOwnable(2L)));
        User blockedUser = User.builder()
                .id(3L)
                .build();
        BlackList blackList = new BlackList(Set.of(blockedUser));

        Object result = handler.handleFiltering(set, blackList);

        assertAll(
                () -> assertThat(result).isSameAs(set),
                () -> assertThat(set).containsExactly(new TestOwnable(1L), new TestOwnable(2L))
        );
    }
}
