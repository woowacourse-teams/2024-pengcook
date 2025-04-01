package net.pengcook.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import net.pengcook.user.exception.BadArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserFollowTest {

    @Test
    @DisplayName("UserFollow 객체를 생성한다")
    void createUserFollow() {
        User follower = new User(1L, "email1", "user1", "nick1", "image1", "region", null, 0, 0);
        User followee = new User(2L, "email2", "user2", "nick2", "image2", "region", null, 0, 0);

        UserFollow userFollow = new UserFollow(follower, followee);

        assertAll(
                () -> assertThat(userFollow.getFollower()).isEqualTo(follower),
                () -> assertThat(userFollow.getFollowee()).isEqualTo(followee)
        );
    }

    @Test
    @DisplayName("자기 자신을 팔로우하면 예외가 발생한다.")
    void follow() {
        User user = new User("email", "userName", "nickName", "image", "region", null);
        assertThatThrownBy(() -> new UserFollow(user, user)).isInstanceOf(BadArgumentException.class);
    }
}
