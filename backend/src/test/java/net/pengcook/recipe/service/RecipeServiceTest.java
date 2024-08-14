package net.pengcook.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Stream;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.dto.IngredientCreateRequest;
import net.pengcook.recipe.dto.MainRecipeResponse;
import net.pengcook.recipe.dto.PageRecipeRequest;
import net.pengcook.recipe.dto.RecipeOfCategoryRequest;
import net.pengcook.recipe.dto.RecipeOfUserRequest;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.exception.InvalidParameterException;
import net.pengcook.recipe.exception.UnauthorizedException;
import net.pengcook.recipe.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/data/recipe.sql")
class RecipeServiceTest {

    public static final int INITIAL_RECIPE_COUNT = 15;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private RecipeRepository recipeRepository;

    static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(0, 2, List.of(2L, 3L)),
                Arguments.of(1, 2, List.of(7L, 9L)),
                Arguments.of(1, 3, List.of(9L, 14L, 15L))
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"0,2,15", "1,2,13", "1,3,12"})
    @DisplayName("요청받은 페이지의 레시피 개요 목록을 조회한다.")
    void readRecipes(int pageNumber, int pageSize, int expectedFirstRecipeId) {
        PageRecipeRequest pageRecipeRequest = new PageRecipeRequest(
                pageNumber, pageSize, null, null, null);
        List<MainRecipeResponse> mainRecipeResponses = recipeService.readRecipes(pageRecipeRequest);

        assertThat(mainRecipeResponses.getFirst().recipeId()).isEqualTo(expectedFirstRecipeId);
    }

    @Test
    @DisplayName("요청받은 페이지 offset 값이 int 타입의 최댓값을 초과하면 예외가 발생한다.")
    void readRecipesWhenPageOffsetIsGreaterThanIntMaxValue() {
        int pageNumber = 1073741824;
        int pageSize = 2;
        PageRecipeRequest pageRecipeRequest = new PageRecipeRequest(
                pageNumber, pageSize, null, null, null);

        assertThatThrownBy(() -> recipeService.readRecipes(pageRecipeRequest))
                .isInstanceOf(InvalidParameterException.class);
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

    @ParameterizedTest
    @MethodSource("provideParameters")
    @DisplayName("특정 카테고리의 레시피를 찾는다.")
    void readRecipesOfCategory(int pageNumber, int pageSize, List<Long> expected) {
        RecipeOfCategoryRequest request = new RecipeOfCategoryRequest("한식", pageNumber, pageSize);

        List<MainRecipeResponse> mainRecipeResponses = recipeService.readRecipesOfCategory(request);
        List<Long> actual = mainRecipeResponses.stream().map(MainRecipeResponse::recipeId).toList();

        assertAll(
                () -> assertThat(actual.size()).isEqualTo(pageSize),
                () -> assertThat(actual).containsAll(expected)
        );
    }

    @Test
    @DisplayName("특정 사용자의 레시피를 찾는다.")
    void readRecipesOfUser() {
        RecipeOfUserRequest request = new RecipeOfUserRequest(1L, 0, 3);
        List<Long> expected = List.of(15L, 14L, 13L);
        int pageSize = 3;

        List<MainRecipeResponse> mainRecipeResponses = recipeService.readRecipesOfUser(request);
        List<Long> actual = mainRecipeResponses.stream().map(MainRecipeResponse::recipeId).toList();

        assertAll(
                () -> assertThat(actual.size()).isEqualTo(pageSize),
                () -> assertThat(actual).containsAll(expected)
        );
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
