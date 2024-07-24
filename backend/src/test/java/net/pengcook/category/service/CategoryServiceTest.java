package net.pengcook.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;
import net.pengcook.category.dto.RecipeOfCategoryRequest;
import net.pengcook.category.repository.CategoryRecipeRepository;
import net.pengcook.category.repository.CategoryRepository;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.dto.MainRecipeResponse;
import net.pengcook.recipe.service.RecipeService;
import net.pengcook.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Import({CategoryService.class, RecipeService.class})
@Sql(scripts = "/data/category.sql")
class CategoryServiceTest {

    private final long INITIAL_CATEGORY_COUNT = 10;
    private final long INITIAL_CATEGORY_RECIPE_COUNT = 25;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryRecipeRepository categoryRecipeRepository;

    @Test
    @DisplayName("레시피의 카테고리를 저장할 수 있다.")
    void saveCategories() {
        User author = new User("ela@pengcook.net", "ela", "엘라", "ela.jpg", LocalDate.of(2024, 7, 22), "KOREA");
        Recipe recipe = new Recipe(1L, "김치볶음밥", author, LocalTime.of(0, 30, 0), "김치볶음밥이미지.jpg", 3, 2, "김치볶음밥 조리법");

        categoryService.saveCategories(recipe, List.of("건강식", "매운음식"));

        assertAll(
                () -> assertThat(categoryRepository.count()).isEqualTo(INITIAL_CATEGORY_COUNT + 1),
                () -> assertThat(categoryRecipeRepository.count()).isEqualTo(INITIAL_CATEGORY_RECIPE_COUNT + 2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    @DisplayName("특정 카테고리의 레시피를 찾는다.")
    void readRecipesOfCategory(int pageNumber, int pageSize, List<Long> expected) {
        RecipeOfCategoryRequest request = new RecipeOfCategoryRequest("한식", pageNumber, pageSize);

        List<MainRecipeResponse> mainRecipeResponses = categoryService.readRecipesOfCategory(request);
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
