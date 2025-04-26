package net.pengcook.block.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.pengcook.block.domain.BlackList;
import net.pengcook.block.domain.Ownable;
import net.pengcook.block.test.TestOwnable;
import net.pengcook.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

class SliceFilteringHandlerTest {

    private final SliceFilteringHandler handler = new SliceFilteringHandler();

    @Test
    @DisplayName("Slice와 Page를 처리한다")
    void canHandleSliceAndPage() {
        Slice<TestOwnable> slice = new SliceImpl<>(
                List.of(new TestOwnable(1L)),
                PageRequest.of(0, 1),
                false
        );
        PageImpl<TestOwnable> page = new PageImpl<>(
                List.of(new TestOwnable(1L)),
                PageRequest.of(0, 1),
                1
        );

        assertAll(
                () -> assertThat(handler.canHandle(slice)).isTrue(),
                () -> assertThat(handler.canHandle(page)).isTrue()
        );
    }

    @Test
    @DisplayName("Slice나 Page가 아니면 처리하지 않는다.")
    void canHandleWhenNonSlice() {
        assertAll(
                () -> assertThat(handler.canHandle("not slice")).isFalse(),
                () -> assertThat(handler.canHandle(null)).isFalse()
        );
    }

    @Test
    @DisplayName("차단된 Ownable이 포함된 Slice는 필터링 한다")
    void handleFilteringWithSliceWhenBlocked() {
        Slice<TestOwnable> slice = new SliceImpl<>(
                new ArrayList<>(List.of(new TestOwnable(1L), new TestOwnable(2L))),
                PageRequest.of(0, 2),
                true
        );
        User blocked = User.builder().id(1L).build();
        BlackList blackList = new BlackList(Set.of(blocked));

        Slice<Ownable> result = handler.handleFiltering(slice, blackList);

        assertThat(result.getContent()).containsExactly(new TestOwnable(2L));
    }

    @Test
    @DisplayName("차단된 Ownable이 포함된 Page는 필터링 한다")
    void handleFilteringWithPageWhenBlocked() {
        Page<TestOwnable> page = new PageImpl<>(
                new ArrayList<>(List.of(new TestOwnable(1L), new TestOwnable(2L))),
                PageRequest.of(0, 2),
                2
        );
        User blocked = User.builder().id(1L).build();
        BlackList blackList = new BlackList(Set.of(blocked));

        Slice<Ownable> result = handler.handleFiltering(page, blackList);

        assertThat(result.getContent()).containsExactly(new TestOwnable(2L));
    }

    @Test
    @DisplayName("차단되지 않은 Ownable만 포함된 Slice는 필터링하지 않는다")
    void handleFilteringWithSliceWhenNotBlocked() {
        Slice<TestOwnable> slice = new SliceImpl<>(
                new ArrayList<>(List.of(new TestOwnable(1L), new TestOwnable(2L))),
                PageRequest.of(0, 2),
                false
        );
        User blocked = User.builder().id(3L).build();
        BlackList blackList = new BlackList(Set.of(blocked));

        Slice<Ownable> result = handler.handleFiltering(slice, blackList);

        assertThat(slice.getContent()).containsExactly(new TestOwnable(1L), new TestOwnable(2L));
    }

    @Test
    @DisplayName("차단되지 않은 Ownable만 포함된 Page는 필터링하지 않는다")
    void handleFilteringWithPageWhenNotBlocked() {
        PageImpl<TestOwnable> page = new PageImpl<>(
                new ArrayList<>(List.of(new TestOwnable(1L), new TestOwnable(2L))),
                PageRequest.of(0, 2),
                2
        );
        User blocked = User.builder().id(3L).build();
        BlackList blackList = new BlackList(Set.of(blocked));

        Slice<Ownable> result = handler.handleFiltering(page, blackList);

        assertThat(page.getContent()).containsExactly(new TestOwnable(1L), new TestOwnable(2L));
    }
}
