package net.pengcook.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.dto.IngredientCreateRequest;
import net.pengcook.recipe.dto.PageRecipeRequest;
import net.pengcook.recipe.dto.RecipeHomeWithMineResponseV1;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.exception.UnauthorizedException;
import net.pengcook.recipe.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/data/recipe.sql")
class RecipeServiceTest {

    private static final int INITIAL_RECIPE_COUNT = 15;

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private RecipeRepository recipeRepository;

    @ParameterizedTest
    @CsvSource(value = {"0,2,15", "1,2,13", "1,3,12"})
    @DisplayName("요청받은 페이지의 레시피 개요 목록을 조회한다.")
    void readRecipes(int pageNumber, int pageSize, int expectedFirstRecipeId) {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        PageRecipeRequest pageRecipeRequest = new PageRecipeRequest(pageNumber, pageSize, null, null, null);
        List<RecipeHomeWithMineResponseV1> recipeHomeWithMineResponses = recipeService.readRecipesV1(userInfo,
                pageRecipeRequest);

        assertThat(recipeHomeWithMineResponses.getFirst().recipeId()).isEqualTo(expectedFirstRecipeId);
    }

    @Test
    @DisplayName("레시피 개요 조회 시 조회자가 작성한 글인지 여부를 함께 응답한다.")
    void readRecipesWithUserInfo() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");
        PageRecipeRequest pageRecipeRequest = new PageRecipeRequest(0, 2, null, null, null);
        List<RecipeHomeWithMineResponseV1> recipeHomeWithMineResponses = recipeService.readRecipesV1(userInfo,
                pageRecipeRequest);

        assertAll(
                () -> assertThat(recipeHomeWithMineResponses.getFirst().mine()).isFalse(),
                () -> assertThat(recipeHomeWithMineResponses.getLast().mine()).isTrue()
        );
    }

    @Test
    @Sql({"/data/recipe.sql", "/data/like.sql"})
    @DisplayName("내가 좋아하는 모든 게시글 개요를 조회한다.")
    void readLikeRecipes() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");

        List<RecipeHomeWithMineResponseV1> actual = recipeService.readLikeRecipesV1(userInfo);

        assertAll(
                () -> assertThat(actual.size()).isOne(),
                () -> assertThat(actual.getFirst().author().authorId()).isEqualTo(userInfo.getId())
        );
    }

    @Test
    @DisplayName("새로운 레시피를 생성한다.")
    void createRecipe() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");

        List<String> categories = List.of("Dessert", "NewCategory");
        List<String> substitutions = List.of("Water", "Orange");
        List<IngredientCreateRequest> ingredients = List.of(
                new IngredientCreateRequest("Apple", Requirement.REQUIRED, substitutions),
                new IngredientCreateRequest("WaterMelon", Requirement.OPTIONAL, null)
        );
        List<RecipeStepRequest> recipeStepRequests = List.of(
                new RecipeStepRequest(null, "스텝1 설명", 1, "00:20:00"),
                new RecipeStepRequest(null, "스텝2 설명", 2, "00:30:00")
        );
        RecipeRequest recipeRequest = new RecipeRequest(
                "새로운 레시피 제목",
                "00:30:00",
                "레시피 썸네일.jpg",
                4,
                "새로운 레시피 설명",
                categories,
                ingredients,
                recipeStepRequests
        );

        RecipeResponse recipe = recipeService.createRecipe(userInfo, recipeRequest);

        assertThat(recipe.recipeId()).isEqualTo(16L);
    }

    @Test
    @DisplayName("레시피를 삭제한다.")
    void deleteRecipe() {
        UserInfo userInfo = new UserInfo(1L, "loki@pengcook.net");

        recipeService.deleteRecipe(userInfo, 1L);

        assertAll(
                () -> assertThat(recipeRepository.findById(1L)).isEmpty(),
                () -> assertThat(recipeRepository.count()).isEqualTo(INITIAL_RECIPE_COUNT - 1)
        );
    }

    @Test
    @DisplayName("본인이 작성하지 않은 레시피 삭제를 시도하면 예외가 발생한다.")
    void deleteRecipeWhenNonAuthor() {
        UserInfo userInfo = new UserInfo(2L, "ela@pengcook.net");

        assertThatThrownBy(() -> recipeService.deleteRecipe(userInfo, 1L))
                .isInstanceOf(UnauthorizedException.class);
    }
}
