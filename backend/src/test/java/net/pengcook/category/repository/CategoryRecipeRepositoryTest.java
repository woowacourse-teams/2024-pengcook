package net.pengcook.category.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql("/data/category.sql")
class CategoryRecipeRepositoryTest {

    @Autowired
    private CategoryRecipeRepository repository;

    @Test
    @DisplayName("요청한 카테고리와 페이지에 해당하는 레시피 id 목록을 반환한다.")
    void findRecipeIdsByCategoryAndKeyword() {
        Pageable pageable = PageRequest.of(0, 3);

        List<Long> recipeIds = repository.findRecipeIdsByCategoryName("한식", pageable);

        assertThat(recipeIds).containsExactlyInAnyOrder(2L, 3L, 7L);
    }
}
