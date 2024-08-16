package net.pengcook.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.comment.repository.CommentRepository;
import net.pengcook.like.repository.RecipeLikeRepository;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.user.domain.BlockedUserGroup;
import net.pengcook.user.domain.UserReport;
import net.pengcook.user.dto.ProfileResponse;
import net.pengcook.user.dto.UpdateProfileRequest;
import net.pengcook.user.dto.UpdateProfileResponse;
import net.pengcook.user.dto.UserBlockResponse;
import net.pengcook.user.dto.UserReportRequest;
import net.pengcook.user.dto.UserResponse;
import net.pengcook.user.dto.UsernameCheckResponse;
import net.pengcook.user.exception.UserNotFoundException;
import net.pengcook.user.repository.UserBlockRepository;
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
                "hello world",
                0L,
                0L,
                15L
        );

        ProfileResponse actual = userService.getUserById(id);

        assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
    }

    @Test
    @DisplayName("존재하지 않는 id의 사용자를 불러오려고 하면 예외가 발생한다.")
    void getUserByIdWhenNotExistId() {
        long id = 2000L;

        assertThatThrownBy(() -> userService.getUserById(id))
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
                "loki_changed.jpg",
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
    void reportUser() {
        UserReportRequest spamReportRequest = new UserReportRequest(
                2,
                "SPAM",
                "스팸 컨텐츠입니다."
        );
        userService.reportUser(1, spamReportRequest);

        UserReport actual = userReportRepository.findById(1L).get();
        assertAll(
                () -> assertThat(actual.getReporter().getId()).isEqualTo(1),
                () -> assertThat(actual.getReportee().getId()).isEqualTo(2),
                () -> assertThat(actual.getReason()).isEqualTo("SPAM"),
                () -> assertThat(actual.getDetails()).isEqualTo("스팸 컨텐츠입니다.")
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

        assertThat(actual).isEqualTo(expected);
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
    @DisplayName("차단한 사용자들의 목록을 불러올 수 있다.")
    void getBlockedUserGroup() {
        long blockerId = 1L;

        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(blockerId);

        assertAll(
                () -> assertThat(blockedUserGroup.isBlocked(2L)).isTrue(),
                () -> assertThat(blockedUserGroup.isBlocked(3L)).isTrue(),
                () -> assertThat(blockedUserGroup.isBlocked(4L)).isFalse()
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
    @DisplayName("사용자를 삭제하면 사용자와 관련있는 차단목록을 지운다.")
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
}
