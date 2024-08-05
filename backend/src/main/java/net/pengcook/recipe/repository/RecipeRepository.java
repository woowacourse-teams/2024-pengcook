package net.pengcook.recipe.repository;

import java.util.List;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.dto.RecipeDataResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("""
            SELECT r.id
            FROM Recipe r
            ORDER BY r.id DESC
            """)
    List<Long> findRecipeIds(Pageable pageable);

    @Query("""
            SELECT new net.pengcook.recipe.dto.RecipeDataResponse(
                r.id,
                r.title,
                r.author.id,
                r.author.username,
                r.author.image,
                r.cookingTime,
                r.thumbnail,
                r.difficulty,
                r.likeCount,
                r.commentCount,
                r.description,
                c.id,
                c.name,
                i.id,
                i.name,
                ir.requirement
            )
            FROM Recipe r
            JOIN FETCH CategoryRecipe cr ON cr.recipe = r
            JOIN FETCH Category c ON cr.category = c
            JOIN FETCH IngredientRecipe ir ON ir.recipe = r
            JOIN FETCH Ingredient i ON ir.ingredient = i
            WHERE r.id IN :recipeIds
            """)
    List<RecipeDataResponse> findRecipeData(List<Long> recipeIds);
}
