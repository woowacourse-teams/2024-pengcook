package net.pengcook.category.repository;

import java.util.List;
import net.pengcook.category.domain.CategoryRecipe;
import net.pengcook.recipe.domain.Recipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRecipeRepository extends JpaRepository<CategoryRecipe, Long> {

    @Query("""
            SELECT cr.recipe.id
            FROM CategoryRecipe cr
            JOIN Category c ON cr.category.id = c.id
            WHERE c.name = :categoryName
            """)
    List<Long> findRecipeIdsByCategoryName(String categoryName, Pageable pageable);

    void deleteByRecipe(Recipe recipe);

    List<CategoryRecipe> findAllByRecipeId(Long recipeId);
}
