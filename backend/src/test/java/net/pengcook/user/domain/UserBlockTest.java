package net.pengcook.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import net.pengcook.user.exception.BadArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserBlockTest {

    @Test
    @DisplayName("UserBlock 객체를 생성한다.")
    void create() {
        User user_loki = new User(1L, "loki@pengcook.net", "loki", "로키", "loki.jpg", "KOREA", null, 0, 0);
        User user_pond = new User(2L, "pond@pengcook.net", "pond", "폰드", "pond.jpg", "KOREA", null, 0, 0);

        UserBlock userBlock = new UserBlock(user_loki, user_pond);

        assertAll(
                () -> assertThat(userBlock.getBlocker()).isEqualTo(user_loki),
                () -> assertThat(userBlock.getBlockee()).isEqualTo(user_pond)
        );
    }

    @Test
    @DisplayName("차단자와 차단대상이 같으면 예외가 발생한다.")
    void createWhenBlockerIsBlockee() {
        User user_loki = new User("loki@pengcook.net", "loki", "로키", "loki.jpg", "KOREA", null);

        assertThatThrownBy(() -> new UserBlock(user_loki, user_loki))
                .isInstanceOf(BadArgumentException.class)
                .hasMessage("자기 자신을 차단할 수 없습니다.");
    }
}
