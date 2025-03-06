package net.pengcook.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.exception.InvalidParameterException;
import net.pengcook.recipe.repository.RecipeStepRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/data/recipe.sql")
class RecipeStepServiceTest {

    private static final int INITIAL_RECIPE_STEP_COUNT = 3;

    @Autowired
    private RecipeStepService recipeStepService;
    @Autowired
    private RecipeStepRepository recipeStepRepository;

    @Test
    @DisplayName("특정 레시피의 스텝을 sequence 순서로 조회한다.")
    void readRecipeSteps() {
        long recipeId = 1L;
        List<RecipeStepResponse> expectedRecipeStepResponses = Arrays.asList(
                new RecipeStepResponse(1L, 1, "레시피1 설명1 이미지", "레시피1 설명1", 1, LocalTime.parse("00:10:00")),
                new RecipeStepResponse(3L, 1, "레시피1 설명2 이미지", "레시피1 설명2", 2, LocalTime.parse("00:20:00")),
                new RecipeStepResponse(2L, 1, "레시피1 설명3 이미지", "레시피1 설명3", 3, LocalTime.parse("00:30:00"))
        );

        List<RecipeStepResponse> recipeStepResponses = recipeStepService.readRecipeSteps(recipeId);

        assertThat(recipeStepResponses).isEqualTo(expectedRecipeStepResponses);
    }

    @Test
    @DisplayName("특정 레시피의 레시피 스텝을 생성한다.")
    void saveRecipeSteps() {
        List<RecipeStepRequest> recipeStepRequests = List.of(
                new RecipeStepRequest("새로운 스텝 이미지1.jpg", "새로운 스텝 설명1", 1, "00:05:00"),
                new RecipeStepRequest("새로운 스텝 이미지2.jpg", "새로운 스텝 설명2", 2, "00:05:00")
        );

        Recipe recipe = new Recipe(2L, null, null, null, null, 0, 0, 0, null, null);
        recipeStepService.saveRecipeSteps(recipe, recipeStepRequests);

        assertThat(recipeStepRepository.count()).isEqualTo(INITIAL_RECIPE_STEP_COUNT + recipeStepRequests.size());
    }

    @Test
    @DisplayName("레시피 스텝 생성 요청 이미지 값이 null이어도 정상적으로 생성하고, 이미지에 null을 저장한다.")
    void saveRecipeStepsWithNullImage() {
        List<RecipeStepRequest> recipeStepRequests = List.of(
                new RecipeStepRequest(null, "새로운 스텝 설명1", 1, "00:05:00")
        );

        Recipe recipe = new Recipe(2L, null, null, null, null, 0, 0, 0, null, null);
        recipeStepService.saveRecipeSteps(recipe, recipeStepRequests);

        assertThat(recipeStepRepository.count()).isEqualTo(INITIAL_RECIPE_STEP_COUNT + recipeStepRequests.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    "})
    @DisplayName("레시피 스텝 생성 요청 이미지 값이 빈 문자열이거나 공백만 있을 경우 예외가 발생한다.")
    void saveRecipeStepsWhenBlankImage(String image) {
        List<RecipeStepRequest> recipeStepRequests = List.of(
                new RecipeStepRequest(image, "새로운 스텝 설명1", 1, "00:05:00")
        );

        Recipe recipe = new Recipe(2L, null, null, null, null, 0, 0, 0, null, null);
        assertThatThrownBy(() -> recipeStepService.saveRecipeSteps(recipe, recipeStepRequests))
                .isInstanceOf(InvalidParameterException.class);
    }

    @Test
    @DisplayName("레시피 스텝 생성 요청 조리시간 값이 null이어도 정상적으로 생성하고, 조리시간에 null을 저장한다.")
    void saveRecipeStepsWithNullCookingTime() {
        List<RecipeStepRequest> recipeStepRequests = List.of(
                new RecipeStepRequest("레시피1 설명1 이미지", "새로운 스텝 설명1", 1, null)
        );

        Recipe recipe = new Recipe(2L, null, null, null, null, 0, 0, 0, null, null);
        recipeStepService.saveRecipeSteps(recipe, recipeStepRequests);

        assertThat(recipeStepRepository.count()).isEqualTo(INITIAL_RECIPE_STEP_COUNT + recipeStepRequests.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "aa:bb", "test"})
    @DisplayName("레시피 스텝 생성 요청 조리시간의 형태가 HH:mm:ss가 아닌 경우 예외가 발생한다.")
    void saveRecipeStepsWhenInappropriateCookingTime(String cookingTime) {
        List<RecipeStepRequest> recipeStepRequests = List.of(
                new RecipeStepRequest("image.jpg", "새로운 스텝 설명1", 1, cookingTime)
        );

        Recipe recipe = new Recipe(2L, null, null, null, null, 0, 0, 0, null, null);
        assertThatThrownBy(() -> recipeStepService.saveRecipeSteps(recipe, recipeStepRequests))
                .isInstanceOf(InvalidParameterException.class);
    }

    @Test
    @DisplayName("레시피에 해당되는 레시피 스텝을 삭제한다.")
    void deleteRecipeStepsByRecipe() {
        recipeStepService.deleteRecipeStepsByRecipe(1L);

        assertThat(recipeStepRepository.count()).isEqualTo(INITIAL_RECIPE_STEP_COUNT - 3);
    }
}
