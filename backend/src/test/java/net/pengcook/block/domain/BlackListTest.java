package net.pengcook.block.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Set;
import net.pengcook.user.domain.User;
import org.junit.jupiter.api.Test;

class BlackListTest {

    @Test
    void contains() {
        User user1 = User.builder()
                .id(1L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .build();
        User user3 = User.builder()
                .id(3L)
                .build();

        BlackList blackList = new BlackList(Set.of(user1, user2));

        assertAll(
                () -> assertThat(blackList.contains(user1.getId())).isTrue(),
                () -> assertThat(blackList.contains(user2.getId())).isTrue(),
                () -> assertThat(blackList.contains(user3.getId())).isFalse()
        );
    }
}
