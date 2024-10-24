package net.pengcook.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import net.pengcook.category.repository.CategoryRecipeRepository;
import net.pengcook.category.repository.CategoryRepository;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Import(CategoryService.class)
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
    @DisplayName("레시피의 카테고리를 저장한다.")
    void saveCategories() {
        User author = new User("ela@pengcook.net", "ela", "엘라", "ela.jpg", "KOREA");
        Recipe recipe = new Recipe(1L, "김치볶음밥", author, LocalTime.of(0, 30, 0), "김치볶음밥이미지.jpg",
                3, 2, 0, "김치볶음밥 조리법", LocalDateTime.now());

        categoryService.saveCategories(recipe, List.of("건강식", "매운음식"));

        assertAll(
                () -> assertThat(categoryRepository.count()).isEqualTo(INITIAL_CATEGORY_COUNT + 1),
                () -> assertThat(categoryRecipeRepository.count()).isEqualTo(INITIAL_CATEGORY_RECIPE_COUNT + 2)
        );
    }

    @Test
    @DisplayName("특정 레시피가 포함된 레시피별 카테고리 정보를 지운다.")
    void deleteCategoryRecipe() {
        User author = new User("ela@pengcook.net", "ela", "엘라", "ela.jpg", "KOREA");
        Recipe recipe = new Recipe(2L, "김밥", author, LocalTime.of(1, 0, 0), "김밥이미지.jpg",
                8, 1, 0, "김밥 조리법", LocalDateTime.of(2024, 7, 2, 13, 0, 0));

        categoryService.deleteCategoryRecipe(recipe);

        assertThat(categoryRecipeRepository.count()).isEqualTo(INITIAL_CATEGORY_RECIPE_COUNT - 2);
    }
}
