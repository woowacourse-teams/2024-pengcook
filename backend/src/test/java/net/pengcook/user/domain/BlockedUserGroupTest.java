package net.pengcook.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Set;
import org.junit.jupiter.api.Test;

class BlockedUserGroupTest {

    @Test
    void isBlocked() {
        User loki = new User(1L, "loki@pengcook.net", "loki", "로키", "loki.jpg", "KOREA", 0, 0);
        User pond = new User(2L, "pond@pengcook.net", "pond", "폰드", "pond.jpg", "KOREA", 0, 0);

        BlockedUserGroup blockedUserGroup = new BlockedUserGroup(Set.of(pond));

        assertAll(
                () -> assertThat(blockedUserGroup.isBlocked(loki.getId())).isFalse(),
                () -> assertThat(blockedUserGroup.isBlocked(pond.getId())).isTrue()
        );
    }
}
