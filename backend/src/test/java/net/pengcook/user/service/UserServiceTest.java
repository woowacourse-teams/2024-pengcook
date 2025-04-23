package net.pengcook.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.comment.repository.CommentRepository;
import net.pengcook.image.service.ImageClientService;
import net.pengcook.like.repository.RecipeLikeRepository;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.user.domain.BlockedUserGroup;
import net.pengcook.user.domain.Reason;
import net.pengcook.user.domain.Type;
import net.pengcook.user.domain.User;
import net.pengcook.user.domain.UserReport;
import net.pengcook.user.dto.ProfileResponse;
import net.pengcook.user.dto.ReportRequest;
import net.pengcook.user.dto.UpdateProfileRequest;
import net.pengcook.user.dto.UpdateProfileResponse;
import net.pengcook.user.dto.UserBlockResponse;
import net.pengcook.user.dto.UserResponse;
import net.pengcook.user.dto.UsernameCheckResponse;
import net.pengcook.user.exception.UserNotFoundException;
import net.pengcook.user.repository.UserBlockRepository;
import net.pengcook.user.repository.UserFollowRepository;
import net.pengcook.user.repository.UserReportRepository;
import net.pengcook.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/data/users.sql")
class UserServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    RecipeLikeRepository recipeLikeRepository;
    @Autowired
    UserBlockRepository userBlockRepository;
    @Autowired
    UserReportRepository userReportRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserFollowService userFollowService;
    @Autowired
    ImageClientService imageClientService;
    @Autowired
    private UserFollowRepository userFollowRepository;

    @Test
    @DisplayName("id를 통해 사용자의 정보를 불러온다.")
    void getUserById() {
        long id = 1L;
        ProfileResponse expected = new ProfileResponse(
                1L,
                "loki@pengcook.net",
                "loki",
                "로키",
                "loki.jpg",
                "KOREA",
                "",
                1L,
                1L,
                15L,
                false
        );

        ProfileResponse actual = userService.getProfile(id, id);

        assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
    }

    @Test
    @DisplayName("id를 통해 팔로우하는 사용자의 정보를 불러온다.")
    void getFollowUserById() {
        long id = 1L;
        ProfileResponse expected = new ProfileResponse(
                1L,
                "loki@pengcook.net",
                "loki",
                "로키",
                "loki.jpg",
                "KOREA",
                "",
                1L,
                1L,
                15L,
                true
        );

        ProfileResponse actual = userService.getProfile(4, id);

        assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
    }

    @Test
    @DisplayName("존재하지 않는 id의 사용자를 불러오려고 하면 예외가 발생한다.")
    void getUserByIdWhenNotExistId() {
        long id = 2000L;

        assertThatThrownBy(() -> userService.getProfile(1, id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자 프로필을 수정한다.")
    void updateProfile() {
        long id = 1L;
        UpdateProfileRequest request = new UpdateProfileRequest(
                "loki_changed",
                "로키_changed",
                "loki_changed.jpg",
                "KOREA",
                "hello world"
        );

        UpdateProfileResponse expected = new UpdateProfileResponse(
                id,
                "loki@pengcook.net",
                "loki_changed",
                "로키_changed",
                imageClientService.getImageUrl("loki_changed.jpg").url(),
                "KOREA",
                "hello world"
        );

        UpdateProfileResponse actual = userService.updateProfile(id, request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("중복되지 않은 username을 입력하면 사용 가능하다")
    void checkUsername() {
        String username = "new_face";

        UsernameCheckResponse usernameCheckResponse = userService.checkUsername(username);

        assertThat(usernameCheckResponse.available()).isTrue();
    }

    @Test
    @DisplayName("중복된 username을 입력하면 사용 불가능하다")
    void checkUsernameWhenDuplicatedUsername() {
        String username = "loki";

        UsernameCheckResponse usernameCheckResponse = userService.checkUsername(username);

        assertThat(usernameCheckResponse.available()).isFalse();
    }

    @Test
    @DisplayName("신고 테이블에 신고 내역을 저장한다.")
    void report() {
        ReportRequest spamReportRequest = new ReportRequest(
                1L,
                Reason.SPAM_CONTENT,
                Type.RECIPE,
                1L,
                null
        );
        userService.report(2, spamReportRequest);

        UserReport actual = userReportRepository.findById(1L).get();
        assertAll(
                () -> assertThat(actual.getReporter().getId()).isEqualTo(2),
                () -> assertThat(actual.getReportee().getId()).isEqualTo(1),
                () -> assertThat(actual.getReason()).isEqualTo(Reason.SPAM_CONTENT),
                () -> assertThat(actual.getType()).isEqualTo(Type.RECIPE),
                () -> assertThat(actual.getTargetId()).isEqualTo(1L),
                () -> assertThat(actual.getDetails()).isNull()
        );
    }

    @Test
    @DisplayName("사용자를 차단한다.")
    void blockUser() {
        long blockerId = 1L;
        long blockeeId = 2L;
        UserBlockResponse expected = new UserBlockResponse(
                new UserResponse(blockerId, "loki@pengcook.net", "loki", "로키", "loki.jpg", "KOREA"),
                new UserResponse(blockeeId, "ela@pengcook.net", "ela", "엘라", "ela.jpg", "KOREA")
        );

        UserBlockResponse actual = userService.blockUser(blockerId, blockeeId);

        assertAll(
                () -> assertThat(actual).isEqualTo(expected),
                () -> assertThat(userBlockRepository.existsByBlockerIdAndBlockeeId(1L, 2L)).isTrue()
        );
    }

    @Test
    @DisplayName("팔로우 관계에 있는 사용자를 차단하면 관련된 팔로우를 제거한다. ")
    void blockUserFollow() {
        long blockerId = 2L;
        long blockeeId = 3L;
        userFollowService.followUser(blockerId, blockeeId);
        User beforeBlockBlockee = userRepository.findById(blockeeId).get();
        User beforeBlockBlocker = userRepository.findById(blockerId).get();
        long initialFollowerCountOfBlockee = beforeBlockBlockee.getFollowerCount();
        long initialFolloweeCountBlocker = beforeBlockBlocker.getFolloweeCount();

        userService.blockUser(blockerId, blockeeId);
        boolean isFollowing = userFollowRepository.existsByFollowerIdAndFolloweeId(blockerId, blockeeId);
        boolean isFollowed = userFollowRepository.existsByFollowerIdAndFolloweeId(blockeeId, blockerId);
        User afterBlockBlockee = userRepository.findById(blockeeId).get();
        User afterBlockBlocker = userRepository.findById(blockerId).get();

        assertAll(
                () -> assertThat(isFollowing).isFalse(),
                () -> assertThat(isFollowed).isFalse(),
                () -> assertThat(afterBlockBlockee.getFollowerCount()).isEqualTo(initialFollowerCountOfBlockee - 1),
                () -> assertThat(afterBlockBlocker.getFolloweeCount()).isEqualTo(initialFolloweeCountBlocker - 1)
        );
    }

    @Test
    @DisplayName("차단하는 사용자가 존재하지 않으면 예외가 발생한다.")
    void blockUserWhenNotExistBlocker() {
        long blockerId = 2000L;
        long blockeeId = 2L;

        assertThatThrownBy(() -> userService.blockUser(blockerId, blockeeId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("정상적으로 로그인되지 않았습니다.");
    }

    @Test
    @DisplayName("차단할 사용자가 존재하지 않으면 예외가 발생한다.")
    void blockUserWhenNotExistBlockee() {
        long blockerId = 1L;
        long blockeeId = 2000L;

        assertThatThrownBy(() -> userService.blockUser(blockerId, blockeeId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("차단할 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("중복된 사용자 차단을 허용하지 않는다")
    void preventDuplicateUserBlock() {
        long blockerId = 1L;
        long blockeeId = 3L;
        int blockCount = userBlockRepository.findAllByBlockerId(blockerId).size();

        assertDoesNotThrow(() -> userService.blockUser(blockerId, blockeeId));
        assertThat(userBlockRepository.findAllByBlockerId(blockerId).size()).isEqualTo(blockCount);
    }

    @Test
    @DisplayName("사용자 차단을 해제한다.")
    void deleteBlock() {
        long blockerId = 1L;
        long blockeeId = 3L;

        userService.deleteBlock(blockerId, blockeeId);

        assertThat(userBlockRepository.existsByBlockerIdAndBlockeeId(1L, 3L)).isFalse();
    }

    @Test
    @DisplayName("차단을 해제하는 사용자가 존재하지 않으면 예외가 발생한다.")
    void deleteBlockWhenNotExistBlocker() {
        long blockerId = 2000L;
        long blockeeId = 3L;

        assertThatThrownBy(() -> userService.deleteBlock(blockerId, blockeeId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("정상적으로 로그인되지 않았습니다.");
    }

    @Test
    @DisplayName("차단당한 사용자가 존재하지 않으면 예외가 발생한다.")
    void deleteBlockWhenNotExistBlockee() {
        long blockerId = 1L;
        long blockeeId = 2000L;

        assertThatThrownBy(() -> userService.deleteBlock(blockerId, blockeeId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("차단한 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("차단 목록을 불러온다.")
    void getBlockeesOf() {
        long blockerId = 1L;
        List<UserBlockResponse> expected = List.of(
                new UserBlockResponse(
                        new UserResponse(blockerId, "loki@pengcook.net", "loki", "로키", "loki.jpg", "KOREA"),
                        new UserResponse(3L, "crocodile@pengcook.net", "crocodile", "악어", "crocodile.jpg", "KOREA")
                ),
                new UserBlockResponse(
                        new UserResponse(blockerId, "loki@pengcook.net", "loki", "로키", "loki.jpg", "KOREA"),
                        new UserResponse(4L, "birdsheep@pengcook.net", "birdsheep", "새양", "birdsheep.jpg", "KOREA")
                ));

        List<UserBlockResponse> userBlockResponses = userService.getBlockeesOf(blockerId);

        assertThat(userBlockResponses).containsExactlyElementsOf(expected);
    }

    @Test
    @DisplayName("차단한 사용자들의 목록을 불러올 수 있다.")
    void getBlockedUserGroup() {
        long blockerId = 1L;

        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(blockerId);

        assertAll(
                () -> assertThat(blockedUserGroup.isBlocked(2L)).isFalse(),
                () -> assertThat(blockedUserGroup.isBlocked(3L)).isTrue(),
                () -> assertThat(blockedUserGroup.isBlocked(4L)).isTrue()
        );
    }

    @Test
    @DisplayName("사용자를 삭제한다.")
    void deleteUser() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");

        userService.deleteUser(userInfo);

        assertThat(userRepository.existsById(userInfo.getId())).isFalse();
    }

    @Test
    @DisplayName("사용자를 삭제하면 사용자가 작성했던 모든 게시글도 지운다.")
    void deleteUserWithRecipes() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");

        userService.deleteUser(userInfo);
        boolean deletedUserRecipes = recipeRepository.findAll().stream()
                .noneMatch(recipe -> recipe.getAuthor().isSameUser(userInfo.getId()));

        assertThat(deletedUserRecipes).isTrue();
    }

    @Test
    @DisplayName("사용자를 삭제하면 사용자가 작성했던 모든 댓글도 지운다.")
    void deleteUserWithComments() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");

        userService.deleteUser(userInfo);
        boolean deletedUserComments = commentRepository.findAll().stream()
                .noneMatch(comment -> comment.getUser().isSameUser(userInfo.getId()));

        assertThat(deletedUserComments).isTrue();
    }

    @Test
    @DisplayName("사용자를 삭제하면 사용자가 작성했던 모든 좋아요도 지운다.")
    void deleteUserWithLikes() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");

        userService.deleteUser(userInfo);
        boolean deletedUserLikes = recipeLikeRepository.findAll().stream()
                .noneMatch(like -> like.getUser().isSameUser(userInfo.getId()));

        assertThat(deletedUserLikes).isTrue();
    }

    @Test
    @DisplayName("사용자를 삭제하면 사용자와 관련있는 차단목록을 지운다.")
    void deleteUserWithBlocks() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");

        userService.deleteUser(userInfo);
        boolean deletedUserBlockee = userBlockRepository.findAll().stream()
                .noneMatch(userBlock -> userBlock.getBlockee().isSameUser(userInfo.getId()));
        boolean deletedUserBlocker = userBlockRepository.findAll().stream()
                .noneMatch(userBlock -> userBlock.getBlocker().isSameUser(userInfo.getId()));

        assertAll(
                () -> assertThat(deletedUserBlockee).isTrue(),
                () -> assertThat(deletedUserBlocker).isTrue()
        );
    }

    @Test
    @DisplayName("사용자를 삭제하면 사용자와 관련있는 신고목록을 지운다.")
    void deleteUserWithUserReports() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");

        userService.deleteUser(userInfo);
        boolean deletedUserReportee = userReportRepository.findAll().stream()
                .noneMatch(userReport -> userReport.getReportee().isSameUser(userInfo.getId()));
        boolean deletedUserReporter = userReportRepository.findAll().stream()
                .noneMatch(userReport -> userReport.getReporter().isSameUser(userInfo.getId()));

        assertAll(
                () -> assertThat(deletedUserReportee).isTrue(),
                () -> assertThat(deletedUserReporter).isTrue()
        );
    }

    @Test
    @DisplayName("사용자를 삭제하면 사용자와 관련있는 팔로우를 지운다.")
    void deleteUserWithUserFollow() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        User beforeDelete = userRepository.findById(4L).get();
        long initialFollowerCount = beforeDelete.getFollowerCount();
        long initialFolloweeCount = beforeDelete.getFolloweeCount();

        userService.deleteUser(userInfo);

        boolean deletedUserFollower = userFollowRepository.findAll().stream()
                .noneMatch(userFollow -> userFollow.getFollower().isSameUser(userInfo.getId()));
        boolean deletedUserFollowee = userFollowRepository.findAll().stream()
                .noneMatch(userFollow -> userFollow.getFollowee().isSameUser(userInfo.getId()));
        User afterDelete = userRepository.findById(4L).get();

        assertAll(
                () -> assertThat(deletedUserFollower).isTrue(),
                () -> assertThat(deletedUserFollowee).isTrue(),
                () -> assertThat(afterDelete.getFollowerCount()).isEqualTo(initialFollowerCount - 1),
                () -> assertThat(afterDelete.getFolloweeCount()).isEqualTo(initialFolloweeCount - 1)
        );
    }
}
