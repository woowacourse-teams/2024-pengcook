package net.pengcook.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.pengcook.category.dto.RecipeOfCategoryRequest;
import net.pengcook.recipe.dto.MainRecipeResponse;
import net.pengcook.recipe.dto.RecipeStepResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Import(RecipeService.class)
@Sql(value = "/data/recipe.sql")
class RecipeServiceTest {

    @Autowired
    private RecipeService recipeService;

    @ParameterizedTest
    @CsvSource(value = {"0,2,15", "1,2,13", "1,3,12"})
    @DisplayName("요청받은 페이지의 레시피 개요 목록을 조회한다.")
    void readRecipes(int pageNumber, int pageSize, int expectedFirstRecipeId) {
        List<MainRecipeResponse> mainRecipeResponses = recipeService.readRecipes(pageNumber, pageSize);

        assertThat(mainRecipeResponses.getFirst().recipeId()).isEqualTo(expectedFirstRecipeId);
    }

    @Test
    @DisplayName("특정 레시피의 스텝을 sequence 순서로 조회한다.")
    void readRecipeSteps() {
        long recipeId = 1L;
        List<RecipeStepResponse> expectedRecipeStepResponses = Arrays.asList(
                new RecipeStepResponse(1L, 1, "레시피1 설명1 이미지", "레시피1 설명1", 1),
                new RecipeStepResponse(3L, 1, "레시피1 설명2 이미지", "레시피1 설명2", 2),
                new RecipeStepResponse(2L, 1, "레시피1 설명3 이미지", "레시피1 설명3", 3)
        );

        List<RecipeStepResponse> recipeStepResponses = recipeService.readRecipeSteps(recipeId);

        assertThat(recipeStepResponses).isEqualTo(expectedRecipeStepResponses);
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
