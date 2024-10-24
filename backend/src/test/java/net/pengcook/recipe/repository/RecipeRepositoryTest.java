package net.pengcook.recipe.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.List;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.dto.RecipeHomeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql("/data/recipe.sql")
class RecipeRepositoryTest {

    @Autowired
    private RecipeRepository repository;

    @Test
    @DisplayName("요청한 페이지에 카테고리 이름으로 레시피 id 목록을 반환한다.")
    void findRecipeIdsByCategory() {
        // given
        Pageable pageable = PageRequest.of(0, 3);
        String category = "한식";

        // when
        List<Long> recipeIds = repository.findRecipeIdsByCategory(pageable, category);

        // then
        assertThat(recipeIds).contains(15L, 14L, 9L);
    }

    @Test
    @DisplayName("요청한 페이지에 키워드로 레시피 id 목록을 반환한다.")
    void findRecipeIdsByKeyword() {
        // given
        Pageable pageable = PageRequest.of(0, 3);
        String keyword = "이크";

        // when
        List<Long> recipeIds = repository.findRecipeIdsByKeyword(pageable, keyword);

        // then
        assertThat(recipeIds).containsExactly(12L, 11L);
    }

    @Test
    @DisplayName("요청한 페이지에 해당하는 레시피 id 목록을 반환한다.")
    void findRecipeIdsByCategoryAndKeyword() {
        Pageable pageable = PageRequest.of(0, 3);

        List<Long> recipeIds = repository.findRecipeIdsByCategoryAndKeyword(pageable, null, null, null);

        assertThat(recipeIds).containsExactly(15L, 14L, 13L);
    }

    @Test
    @DisplayName("레시피 id에 해당되는 세부 정보를 반환한다.")
    void findRecipeData() {
        List<Long> recipeIds = List.of(4L, 3L);
        RecipeHomeResponse expectedData = new RecipeHomeResponse(4, "토마토스파게티", 1, "loki", "loki.jpg",
                "토마토스파게티이미지.jpg", 2, 0, LocalDateTime.of(2024, 7, 2, 13, 0, 0));

        List<RecipeHomeResponse> recipeData = repository.findRecipeDataV1(recipeIds);

        assertAll(
                () -> assertThat(recipeData).hasSize(2),
                () -> assertThat(recipeData).contains(expectedData)
        );
    }

    @Test
    @DisplayName("사용자 id로 작성된 레시피 id 목록을 최신 순으로 반환한다.")
    void findRecipeIdsByUserId() {
        Pageable pageable = PageRequest.of(0, 3);

        List<Long> recipeIds = repository.findRecipeByAuthorIdOrderByIdDesc(pageable, 1L).stream()
                .map(Recipe::getId)
                .toList();

        assertThat(recipeIds).containsExactly(14L, 13L, 12L);
    }
}
