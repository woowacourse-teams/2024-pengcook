package net.pengcook.recipe.repository;

import static net.pengcook.ingredient.domain.Requirement.REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import net.pengcook.recipe.dto.RecipeDataResponse;
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
        RecipeDataResponse expectedData = new RecipeDataResponse(4, "토마토스파게티", 1, "loki", "loki.jpg",
                LocalTime.of(0, 30), "토마토스파게티이미지.jpg", 3, 2, 0, "토마토스파게티 조리법", LocalDateTime.of(2024, 7, 2, 13, 0, 0),
                2, "양식", 2, "쌀", REQUIRED
        );

        List<RecipeDataResponse> recipeData = repository.findRecipeData(recipeIds);

        assertAll(
                () -> assertThat(recipeData).hasSize(6 + 3),
                () -> assertThat(recipeData).contains(expectedData)
        );
    }

    @Test
    @DisplayName("사용자 id로 작성된 레시피 id 목록을 최신 순으로 반환한다.")
    void findRecipeIdsByUserId() {
        Pageable pageable = PageRequest.of(0, 3);

        List<Long> recipeIds = repository.findRecipeIdsByUserId(1L, pageable);

        assertThat(recipeIds).containsExactly(14L, 13L, 12L);
    }
}
