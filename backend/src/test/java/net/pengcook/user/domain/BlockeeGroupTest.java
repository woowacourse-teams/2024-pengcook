package net.pengcook.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Set;
import org.junit.jupiter.api.Test;

class BlockeeGroupTest {

    @Test
    void isBlocked() {
        User loki = new User(1L, "loki@pengcook.net", "loki", "로키", "loki.jpg", "KOREA", null, 0, 0);
        User pond = new User(2L, "pond@pengcook.net", "pond", "폰드", "pond.jpg", "KOREA", null, 0, 0);

        BlockeeGroup blockeeGroup = new BlockeeGroup(Set.of(pond));

        assertAll(
                () -> assertThat(blockeeGroup.contains(loki.getId())).isFalse(),
                () -> assertThat(blockeeGroup.contains(pond.getId())).isTrue()
        );
    }
}
