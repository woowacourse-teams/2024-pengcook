package net.pengcook.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import net.pengcook.recipe.dto.MainRecipeRequest;
import net.pengcook.recipe.dto.MainRecipeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    @CsvSource(value = {"0,2,4", "1,2,2", "1,3,1"})
    @DisplayName("요청받은 페이지의 레시피 개요 목록을 조회한다.")
    void readRecipes(int pageNumber, int pageSize, int expectedFirstRecipeId) {
        MainRecipeRequest request = new MainRecipeRequest(pageNumber, pageSize);

        List<MainRecipeResponse> mainRecipeResponses = recipeService.readRecipes(request);

        assertThat(mainRecipeResponses.getFirst().recipeId()).isEqualTo(expectedFirstRecipeId);
    }
}
