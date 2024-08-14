package net.pengcook.ingredient.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.ingredient.domain.Ingredient;
import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.repository.IngredientRecipeRepository;
import net.pengcook.recipe.domain.Recipe;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientRecipeService {

    private final IngredientRecipeRepository ingredientRecipeRepository;
    private final IngredientSubstitutionService ingredientSubstitutionService;

    public IngredientRecipe save(Ingredient ingredient, Recipe recipe, Requirement requirement) {
        IngredientRecipe ingredientRecipe = new IngredientRecipe(ingredient, recipe, requirement);
        return ingredientRecipeRepository.save(ingredientRecipe);
    }

    public void deleteIngredientRecipe(long recipeId) {
        List<IngredientRecipe> ingredientRecipes = ingredientRecipeRepository.findAllByRecipeId(recipeId);
        for (IngredientRecipe ingredientRecipe : ingredientRecipes) {
            ingredientSubstitutionService.delete(ingredientRecipe);
            ingredientRecipeRepository.delete(ingredientRecipe);
        }
    }
}

