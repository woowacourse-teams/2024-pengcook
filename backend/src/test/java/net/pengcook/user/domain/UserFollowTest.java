package net.pengcook.user.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import net.pengcook.user.exception.BadArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserFollowTest {

    @Test
    @DisplayName("자기 자신을 팔로우하면 예외가 발생한다.")
    void follow() {
        User user = new User("email", "userName", "nickName", "image", "region");
        assertThatThrownBy(() -> new UserFollow(user, user)).isInstanceOf(BadArgumentException.class);
    }
}
