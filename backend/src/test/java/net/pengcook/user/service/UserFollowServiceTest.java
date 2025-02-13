package net.pengcook.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import net.pengcook.user.domain.UserFollow;
import net.pengcook.user.dto.FollowInfoResponse;
import net.pengcook.user.dto.FollowUserInfoResponse;
import net.pengcook.user.dto.UserFollowResponse;
import net.pengcook.user.exception.IllegalStateException;
import net.pengcook.user.repository.UserFollowRepository;
import net.pengcook.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/data/users.sql")
class UserFollowServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserFollowRepository userFollowRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserFollowService userFollowService;

    @Test
    @DisplayName("사용자를 팔로우한다.")
    void follow() {
        long followerId = 1L;
        long followeeId = 6L;
        UserFollowResponse expected = new UserFollowResponse(1, 6);

        UserFollowResponse actual = userFollowService.followUser(followerId, followeeId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("차단 관계에 있는 사용자를 팔로우하면 예외가 발생한다.")
    void followUserWhenBlockingOrBlocked() {
        long followerId = 1L;
        long blockingFolloweeId = 2L;
        long blockedFolloweeId = 5L;

        assertThatThrownBy(() -> userFollowService.followUser(followerId, blockingFolloweeId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("팔로우 할 수 없습니다.");
        assertThatThrownBy(() -> userFollowService.followUser(followerId, blockedFolloweeId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("팔로우 할 수 없습니다.");
    }

    @Test
    @DisplayName("사용자를 언팔로우한다.")
    void unfollow() {
        long followerId = 1L;
        long followeeId = 4L;
        List<UserFollow> beforeUnfollow = userFollowRepository.findAllByFollowerId(followerId);

        userFollowService.unfollowUser(followerId, followeeId);

        List<UserFollow> afterUnfollow = userFollowRepository.findAllByFollowerId(followerId);
        assertThat(afterUnfollow.size()).isEqualTo(beforeUnfollow.size() - 1);
    }

    @Test
    @DisplayName("팔로워 목록을 조회한다")
    void getFollowerInfo() {
        long userId = 1L;
        List<FollowUserInfoResponse> followUserInfoResponse = List.of(
                new FollowUserInfoResponse("birdsheep", "birdsheep.jpg")
        );
        FollowInfoResponse expected = new FollowInfoResponse(
                followUserInfoResponse,
                1
        );

        FollowInfoResponse actualFollower = userFollowService.getFollowerInfo(userId);

        assertThat(actualFollower).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("팔로잉 목록을 조회한다")
    void getFollowingInfo() {
        long userId = 1L;
        List<FollowUserInfoResponse> followUserInfoResponse = List.of(
                new FollowUserInfoResponse("birdsheep", "birdsheep.jpg")
        );
        FollowInfoResponse expected = new FollowInfoResponse(
                followUserInfoResponse,
                1
        );

        FollowInfoResponse actualFollowee = userFollowService.getFollowingInfo(userId);

        assertThat(actualFollowee).usingRecursiveComparison().isEqualTo(expected);
    }
}
