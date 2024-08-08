package net.pengcook.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.exception.InvalidParameterException;
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

    @Autowired
    private RecipeStepService recipeStepService;

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
    @DisplayName("특정 레시피의 특정 레시피 스텝을 조회한다.")
    void readRecipeStep() {
        RecipeStepResponse recipeStepResponse = recipeStepService.readRecipeStep(1L, 1L);

        assertAll(
                () -> assertThat(recipeStepResponse.recipeId()).isEqualTo(1L),
                () -> assertThat(recipeStepResponse.description()).isEqualTo("레시피1 설명1")
        );

    }

    @Test
    @DisplayName("특정 레시피의 레시피 스텝을 생성한다.")
    void createRecipeStep() {
        RecipeStepRequest recipeStepRequest = new RecipeStepRequest("새로운 스텝 이미지.jpg", "새로운 스텝 설명", 1, "00:05:00");

        RecipeStepResponse recipeStepResponse = recipeStepService.createRecipeStep(2L, recipeStepRequest);

        assertAll(
                () -> assertThat(recipeStepResponse.recipeId()).isEqualTo(2L),
                () -> assertThat(recipeStepResponse.description()).isEqualTo("새로운 스텝 설명")
        );
    }

    @Test
    @DisplayName("레시피 스텝 생성 요청 이미지 값이 null이어도 정상적으로 생성하고, 이미지에 null을 저장한다.")
    void createRecipeStepWithNullImage() {
        RecipeStepRequest recipeStepRequest = new RecipeStepRequest(null, "새로운 스텝 설명", 1, "00:05:00");

        RecipeStepResponse recipeStepResponse = recipeStepService.createRecipeStep(2L, recipeStepRequest);

        assertAll(
                () -> assertThat(recipeStepResponse.recipeId()).isEqualTo(2L),
                () -> assertThat(recipeStepResponse.image()).isNull(),
                () -> assertThat(recipeStepResponse.description()).isEqualTo("새로운 스텝 설명")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    "})
    @DisplayName("레시피 스텝 생성 요청 이미지 값이 빈 문자열이거나 공백만 있을 경우 예외가 발생한다.")
    void createRecipeStepWhenBlankImage(String image) {
        RecipeStepRequest recipeStepRequest = new RecipeStepRequest(image, "새로운 스텝 설명", 1, "00:05:00");

        assertThatThrownBy(() -> recipeStepService.createRecipeStep(2L, recipeStepRequest))
                .isInstanceOf(InvalidParameterException.class);
    }

    @Test
    @DisplayName("레시피 스텝 생성 요청 조리시간 값이 null이어도 정상적으로 생성하고, 조리시간에 null을 저장한다.")
    void createRecipeStepWithNullCookingTime() {
        RecipeStepRequest recipeStepRequest = new RecipeStepRequest("image.jpg", "스텝 설명", 1, null);

        RecipeStepResponse recipeStepResponse = recipeStepService.createRecipeStep(2L, recipeStepRequest);

        assertAll(
                () -> assertThat(recipeStepResponse.recipeId()).isEqualTo(2L),
                () -> assertThat(recipeStepResponse.cookingTime()).isNull()
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "aa:bb", "test"})
    @DisplayName("레시피 스텝 생성 요청 조리시간의 형태가 HH:mm:ss가 아닌 경우 예외가 발생한다.")
    void createRecipeStepWhenInappropriateCookingTime(String cookingTime) {
        RecipeStepRequest recipeStepRequest = new RecipeStepRequest("image.jpg", "새로운 스텝 설명", 1, cookingTime);

        assertThatThrownBy(() -> recipeStepService.createRecipeStep(2L, recipeStepRequest))
                .isInstanceOf(InvalidParameterException.class);
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

        RecipeStepResponse recipeStepResponse = recipeStepService.createRecipeStep(1L, recipeStepRequest);

        assertAll(
                () -> assertThat(recipeStepResponse.id()).isEqualTo(1L),
                () -> assertThat(recipeStepResponse.description()).isEqualTo("changedDescription"),
                () -> assertThat(recipeStepResponse.image()).endsWith("changedImage.jpg")
        );
    }

    @Test
    @DisplayName("레시피 스텝 등록 시 이전 sequence 정보가 없으면 예외가 발생한다.")
    void createRecipeWhenPreviousSequenceDoesNotExist() {
        RecipeStepRequest recipeStepRequest = new RecipeStepRequest("새로운 스텝 이미지.jpg", "새로운 스텝 설명", 2, "00:05:00");

        assertThatThrownBy(() -> recipeStepService.createRecipeStep(2L, recipeStepRequest))
                .isInstanceOf(InvalidParameterException.class);
    }
}
