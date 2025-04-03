package net.pengcook.like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.like.exception.RecipeNotFoundException;
import net.pengcook.like.exception.UserNotFoundException;
import net.pengcook.like.repository.RecipeLikeRepository;
import net.pengcook.recipe.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@Sql("/data/like.sql")
@Import(RecipeLikeService.class)
@DataJpaTest
class RecipeLikeServiceTest {

    @Autowired
    private RecipeLikeService recipeLikeService;
    @Autowired
    private RecipeLikeRepository likeRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @DisplayName("내가 좋아요를 한 게시글인지 조회한다.")
    void readLikeMine() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        long recipeId = 2L;

        boolean like = recipeLikeService.readLike(userInfo, recipeId).isLike();

        assertThat(like).isTrue();
    }

    @Test
    @DisplayName("내가 좋아요를 하지 않은 게시글인지 조회한다.")
    void readLikeNotMine() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        long recipeId = 3L;

        boolean like = recipeLikeService.readLike(userInfo, recipeId).isLike();

        assertThat(like).isFalse();
    }

    @Test
    @DisplayName("좋아요가 없는 게시글에 좋아요를 추가한다.")
    void addLikeOnEmpty() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        long recipeId = 3L;
        int prevLikeCount = recipeRepository.findById(recipeId).orElseThrow().getLikeCount();

        recipeLikeService.addLike(userInfo, recipeId);
        boolean exists = likeRepository.existsByUserIdAndRecipeId(userInfo.getId(), recipeId);
        int curLikeCount = recipeRepository.findById(recipeId).orElseThrow().getLikeCount();

        assertThat(exists).isTrue();
        assertThat(curLikeCount).isEqualTo(prevLikeCount + 1);
    }

    @Test
    @DisplayName("좋아요가 없는 게시글에 좋아요를 취소해도 아무일이 일어나지 않는다.")
    void deleteLikeOnEmpty() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        long recipeId = 3L;
        int prevLikeCount = recipeRepository.findById(recipeId).orElseThrow().getLikeCount();

        recipeLikeService.deleteLike(userInfo, recipeId);
        boolean exists = likeRepository.existsByUserIdAndRecipeId(userInfo.getId(), recipeId);
        int curLikeCount = recipeRepository.findById(recipeId).orElseThrow().getLikeCount();

        assertThat(exists).isFalse();
        assertThat(curLikeCount).isEqualTo(prevLikeCount);
    }

    @Test
    @DisplayName("좋아요를 한 게시글에 좋아요를 추가해도 아무일이 일어나지 않는다.")
    void addLikeOnPresent() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        long recipeId = 2L;
        int prevLikeCount = recipeRepository.findById(recipeId).orElseThrow().getLikeCount();

        recipeLikeService.addLike(userInfo, recipeId);
        boolean exists = likeRepository.existsByUserIdAndRecipeId(userInfo.getId(), recipeId);
        int curLikeCount = recipeRepository.findById(recipeId).orElseThrow().getLikeCount();

        assertThat(exists).isTrue();
        assertThat(curLikeCount).isEqualTo(prevLikeCount);
    }

    @Test
    @DisplayName("좋아요를 한 게시글에 좋아요를 취소한다.")
    void deleteLikeOnPresent() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        long recipeId = 2L;
        int prevLikeCount = recipeRepository.findById(recipeId).orElseThrow().getLikeCount();

        recipeLikeService.deleteLike(userInfo, recipeId);
        boolean exists = likeRepository.existsByUserIdAndRecipeId(userInfo.getId(), recipeId);
        int curLikeCount = recipeRepository.findById(recipeId).orElseThrow().getLikeCount();

        assertThat(exists).isFalse();
        assertThat(curLikeCount).isEqualTo(prevLikeCount - 1);
    }

    @Test
    @DisplayName("존재하지 않는 유저가 레시피의 좋아요를 추가할 경우 예외가 발생한다.")
    void addLikeWhenNotExistUser() {
        UserInfo userInfo = new UserInfo(999L, "annoymouse@pengcook.net");
        long recipeId = 2L;

        assertThatThrownBy(() -> recipeLikeService.addLike(userInfo, recipeId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 레시피에 대해 좋아요를 추가할 경우 예외가 발생한다.")
    void addLikeWhenNotExistRecipe() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        long recipeId = 999L;

        assertThatThrownBy(() -> recipeLikeService.addLike(userInfo, recipeId))
                .isInstanceOf(RecipeNotFoundException.class);
    }

    @Test
    @DisplayName("해당 레시피의 모든 좋아요를 삭제한다.")
    void deleteLikesByRecipe() {
        long recipeId = 2L;
        long expectedLikeCount = likeRepository.count() - 2;

        recipeLikeService.deleteLikesByRecipe(recipeId);
        long actualLikeCount = likeRepository.count();

        assertThat(actualLikeCount).isEqualTo(expectedLikeCount);
    }

    @Test
    @DisplayName("해당 사용자의 모든 좋아요를 삭제한다.")
    void deleteLikesByUser() {
        long userId = 2L;
        long actualLikeCount = likeRepository.count() - 2;

        recipeLikeService.deleteLikesByUser(userId);
        long expectedLikeCount = likeRepository.count();

        assertThat(actualLikeCount).isEqualTo(expectedLikeCount);
    }

    @Test
    @DisplayName("해당 사용자가 좋아요한 레시피 아이디를 조회한다.")
    void readLikedRecipeIdsByUser() {
        long userId = 2L;
        List<Long> expected = List.of(2L, 5L);

        List<Long> actual = recipeLikeService.readLikedRecipeIdsByUser(userId);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "1, false",
            "2, true"
    })
    @DisplayName("해당 사용자가 해당 레시피에 좋아요를 눌렀는지 확인한다.")
    void isLike(long userId, boolean expected) {
        long recipeId = 5L;

        boolean actual = recipeLikeService.isLike(recipeId, userId);

        assertThat(actual).isEqualTo(expected);
    }
}
