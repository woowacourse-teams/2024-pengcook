package net.pengcook.like.service;

import net.pengcook.authentication.domain.JwtTokenManager;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.like.domain.RecipeLike;
import net.pengcook.like.repository.RecipeLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/data/like.sql")
@Import({JwtTokenManager.class, RecipeLikeService.class})
@DataJpaTest
class RecipeLikeServiceTest {

    @Autowired
    private RecipeLikeService recipeLikeService;
    @Autowired
    private RecipeLikeRepository likeRepository;

    @Test
    @DisplayName("좋아요가 없는 게시글에 좋아요를 추가한다.")
    void toggleLikeOnEmpty() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        long recipeId = 2L;

        recipeLikeService.toggleLike(userInfo, recipeId);
        Optional<RecipeLike> like = likeRepository.findByUserIdAndRecipeId(userInfo.getId(), recipeId);

        assertThat(like).isPresent();
    }

    @Test
    @DisplayName("이미 좋아요를 한 게시글에 좋아요를 취소한다.")
    void toggleLikeOnPresent() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        long recipeId = 1L;

        recipeLikeService.toggleLike(userInfo, recipeId);
        Optional<RecipeLike> like = likeRepository.findByUserIdAndRecipeId(userInfo.getId(), recipeId);

        assertThat(like).isEmpty();
    }
}
