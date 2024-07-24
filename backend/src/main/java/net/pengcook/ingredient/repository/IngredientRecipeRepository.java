package net.pengcook.ingredient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.pengcook.ingredient.domain.IngredientRecipe;

@Repository
public interface IngredientRecipeRepository extends JpaRepository<IngredientRecipe, Long> {
}
