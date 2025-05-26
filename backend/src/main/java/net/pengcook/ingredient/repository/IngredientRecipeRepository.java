package net.pengcook.ingredient.repository;

import java.util.List;
import net.pengcook.ingredient.domain.IngredientRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRecipeRepository extends JpaRepository<IngredientRecipe, Long> {

    List<IngredientRecipe> findAllByRecipeId(long recipeId);
}
