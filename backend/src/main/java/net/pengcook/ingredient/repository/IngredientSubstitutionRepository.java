package net.pengcook.ingredient.repository;

import java.util.List;
import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.ingredient.domain.IngredientSubstitution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientSubstitutionRepository extends JpaRepository<IngredientSubstitution, Long> {

    List<IngredientSubstitution> findAllByIngredientRecipe(IngredientRecipe ingredientRecipe);
}
