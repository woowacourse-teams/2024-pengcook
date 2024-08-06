package net.pengcook.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.category.dto.RecipeOfCategoryRequest;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.dto.IngredientCreateRequest;
import net.pengcook.recipe.dto.MainRecipeResponse;
import net.pengcook.recipe.dto.PageRecipeRequest;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.exception.InvalidParameterException;
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

    @Autowired
    private RecipeService recipeService;

    @ParameterizedTest
    @CsvSource(value = {"0,2,15", "1,2,13", "1,3,12"})
    @DisplayName("요청받은 페이지의 레시피 개요 목록을 조회한다.")
    void readRecipes(int pageNumber, int pageSize, int expectedFirstRecipeId) {
        PageRecipeRequest pageRecipeRequest = new PageRecipeRequest(pageNumber, pageSize);
        List<MainRecipeResponse> mainRecipeResponses = recipeService.readRecipes(pageRecipeRequest);

        assertThat(mainRecipeResponses.getFirst().recipeId()).isEqualTo(expectedFirstRecipeId);
    }

    @Test
    @DisplayName("요청받은 페이지 offset 값이 int 타입의 최댓값을 초과하면 예외가 발생한다.")
    void readRecipesWhenPageOffsetIsGreaterThanIntMaxValue() {
        int pageNumber = 1073741824;
        int pageSize = 2;
        PageRecipeRequest pageRecipeRequest = new PageRecipeRequest(pageNumber, pageSize);

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
        RecipeRequest recipeRequest = new RecipeRequest(
                "새로운 레시피 제목",
                "00:30:00",
                "레시피 썸네일.jpg",
                4,
                "새로운 레시피 설명",
                categories,
                ingredients
        );

        RecipeResponse recipe = recipeService.createRecipe(userInfo, recipeRequest);

        assertThat(recipe.recipeId()).isEqualTo(16L);
    }

    @Test
    @DisplayName("특정 레시피의 스텝을 sequence 순서로 조회한다.")
    void readRecipeSteps() {
        long recipeId = 1L;
        List<RecipeStepResponse> expectedRecipeStepResponses = Arrays.asList(
                new RecipeStepResponse(1L, 1, "레시피1 설명1 이미지", "레시피1 설명1", 1, LocalTime.parse("00:10:00")),
                new RecipeStepResponse(3L, 1, "레시피1 설명2 이미지", "레시피1 설명2", 2, LocalTime.parse("00:20:00")),
                new RecipeStepResponse(2L, 1, "레시피1 설명3 이미지", "레시피1 설명3", 3, LocalTime.parse("00:30:00"))
        );

        List<RecipeStepResponse> recipeStepResponses = recipeService.readRecipeSteps(recipeId);

        assertThat(recipeStepResponses).isEqualTo(expectedRecipeStepResponses);
    }

    @Test
    @DisplayName("특정 레시피의 특정 레시피 스텝을 조회한다.")
    void readRecipeStep() {
        RecipeStepResponse recipeStepResponse = recipeService.readRecipeStep(1L, 1L);

        assertAll(
                () -> assertThat(recipeStepResponse.recipeId()).isEqualTo(1L),
                () -> assertThat(recipeStepResponse.description()).isEqualTo("레시피1 설명1")
        );

    }

    @Test
    @DisplayName("특정 레시피의 레시피 스텝을 생성한다.")
    void createRecipeStep() {
        RecipeStepRequest recipeStepRequest = new RecipeStepRequest("새로운 스텝 이미지.jpg", "새로운 스텝 설명", 1, "00:05:00");

        RecipeStepResponse recipeStepResponse = recipeService.createRecipeStep(2L, recipeStepRequest);

        assertAll(
                () -> assertThat(recipeStepResponse.recipeId()).isEqualTo(2L),
                () -> assertThat(recipeStepResponse.description()).isEqualTo("새로운 스텝 설명")
        );
    }

    @Test
    @DisplayName("레시피 스텝 등록 시 이미 존재하는 정보가 있으면 새로운 내용으로 수정한다.")
    void createRecipeStepWithExistingRecipeStep() {
        RecipeStepRequest recipeStepRequest = new RecipeStepRequest(
                "changedImage.jpg",
                "changedDescription",
                1,
                "00:05:00"
        );

        RecipeStepResponse recipeStepResponse = recipeService.createRecipeStep(1L, recipeStepRequest);

        assertAll(
                () -> assertThat(recipeStepResponse.id()).isEqualTo(1L),
                () -> assertThat(recipeStepResponse.description()).isEqualTo("changedDescription"),
                () -> assertThat(recipeStepResponse.image()).endsWith("changedImage.jpg")
        );
    }

    @Test
    @DisplayName("레시피 스텝 등록 시 이전 sequence 정보가 없으면 예외가 발생한다.")
    void createRecipeWhenPreviousSequenceIsNotExist() {
        RecipeStepRequest recipeStepRequest = new RecipeStepRequest("새로운 스텝 이미지.jpg", "새로운 스텝 설명", 2, "00:05:00");

        assertThatThrownBy(() -> recipeService.createRecipeStep(2L, recipeStepRequest))
                .isInstanceOf(InvalidParameterException.class);
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

    static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(0, 2, List.of(2L, 3L)),
                Arguments.of(1, 2, List.of(7L, 9L)),
                Arguments.of(1, 3, List.of(9L, 14L, 15L))
        );
    }
}
