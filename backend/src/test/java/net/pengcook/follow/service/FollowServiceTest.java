package net.pengcook.follow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.follow.entity.Follow;
import net.pengcook.follow.repository.FollowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@Sql("/data/follow.sql")
@Import({FollowService.class})
@DataJpaTest
class FollowServiceTest {

    @Autowired
    FollowService followService;
    @Autowired
    FollowRepository followRepository;

    @Test
    @DisplayName("한 사람이 다른 사람을 팔로우 한다.")
    void follow() {
        UserInfo userInfo = new UserInfo(1, "ela@pengcook.net");
        int followeeId = 2;

        followService.follow(userInfo, followeeId);
        Follow lastFollow = followRepository.findAll().getLast();

        assertAll(
                () -> assertThat(lastFollow.getFollower().getId()).isEqualTo(userInfo.getId()),
                () -> assertThat(lastFollow.getFollowee().getId()).isEqualTo(followeeId)
        );
    }
}
