package net.pengcook.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Import(RecipeService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/data.sql")
class RecipeServiceTest {

    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    RecipeService recipeService;

    @Test
    @DisplayName("레시피의 정보를 불러올 수 있다.")
    void readRecipe() {
        long id = 1L;
        RecipeResponse expected = new RecipeResponse(
                "Steamed Tofu with Shrimp and Eggs",
                "http://www.foodsafetykorea.go.kr/uploadimg/cook/10_00028_2.png",
                "https://avatars.githubusercontent.com/u/22692687?v=4",
                "Seyang",
                List.of("Korean food"),
                List.of("Soft Tofu", "Cocktail Shrimp", "Egg", "Sugar", "Unsalted Butter"),
                List.of("Heavy Cream", "Spinach"),
                5,
                LocalTime.parse("00:30:00"),
                1534,
                36056,
                "This is steamed Tofu with Shrimp and Eggs."
        );

        RecipeResponse actual = recipeService.readRecipe(id);

        assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
    }

    @Test
    @DisplayName("존재하지 않는 레시피의 정보를 불러오려고 하면 예외가 발생한다.")
    void readRecipeWhenNotExistId() {
        long id = 2000L;

        assertThatThrownBy(() -> recipeService.readRecipe(id))
                .isInstanceOf(NoSuchElementException.class);
    }
}
