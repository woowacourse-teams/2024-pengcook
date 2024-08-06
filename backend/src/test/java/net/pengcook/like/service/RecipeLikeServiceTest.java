package net.pengcook.like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.like.domain.RecipeLike;
import net.pengcook.like.exception.RecipeLikeException;
import net.pengcook.like.repository.RecipeLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@Sql(value = "/data/like.sql")
@Import(RecipeLikeService.class)
@DataJpaTest
class RecipeLikeServiceTest {

    @Autowired
    private RecipeLikeService recipeLikeService;
    @Autowired
    private RecipeLikeRepository likeRepository;

    @Test
    @DisplayName("레시피의 좋아요 개수를 조회한다.")
    void readLikesCountOne() {
        long recipeId = 1L;

        int likesCount = recipeLikeService.readLikesCount(recipeId).likesCount();

        assertThat(likesCount).isOne();
    }

    @Test
    @DisplayName("레시피의 좋아요 개수를 조회한다.")
    void readLikesCountZero() {
        long recipeId = 2L;

        int likesCount = recipeLikeService.readLikesCount(recipeId).likesCount();

        assertThat(likesCount).isZero();
    }

    @Test
    @DisplayName("존재하지 않는 레시피에 좋아요를 조회할 경우 예외가 발생한다.")
    void readLikesWhenNotExistRecipe() {
        long recipeId = 7L;

        assertThatThrownBy(() -> recipeLikeService.readLikesCount(recipeId))
                .isInstanceOf(RecipeLikeException.class)
                .hasMessage("존재하지 않는 레시피 입니다.");
    }

    @Test
    @DisplayName("좋아요가 없는 게시글에 좋아요를 추가한다.")
    void toggleLikeOnEmpty() {
        UserInfo userInfo = new UserInfo(1L, "ela@pengcook.net");
        long recipeId = 2L;

        recipeLikeService.toggleLike(userInfo, recipeId);
        Optional<RecipeLike> like = likeRepository.findByUserIdAndRecipeId(userInfo.getId(), recipeId);

        assertThat(like).isPresent();
    }

    @Test
    @DisplayName("이미 좋아요를 한 게시글에 좋아요를 취소한다.")
    void toggleLikeOnPresent() {
        UserInfo userInfo = new UserInfo(1L, "ela@pengcook.net");
        long recipeId = 1L;

        recipeLikeService.toggleLike(userInfo, recipeId);
        Optional<RecipeLike> like = likeRepository.findByUserIdAndRecipeId(userInfo.getId(), recipeId);

        assertThat(like).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 유저가 레시피의 좋아요를 변경할 경우 예외가 발생한다.")
    void toggleLikeWhenNotExistUser() {
        UserInfo userInfo = new UserInfo(4L, "seyang@pengcook.net");
        long recipeId = 1L;

        assertThatThrownBy(() -> recipeLikeService.toggleLike(userInfo, recipeId))
                .isInstanceOf(RecipeLikeException.class)
                .hasMessage("존재하지 않는 유저 입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 레시피에 대해 좋아요를 변경할 경우 예외가 발생한다.")
    void toggleLikeWhenNotExistRecipe() {
        UserInfo userInfo = new UserInfo(1L, "ela@pengcook.net");
        long recipeId = 7L;

        assertThatThrownBy(() -> recipeLikeService.toggleLike(userInfo, recipeId))
                .isInstanceOf(RecipeLikeException.class)
                .hasMessage("존재하지 않는 레시피 입니다.");
    }
}
