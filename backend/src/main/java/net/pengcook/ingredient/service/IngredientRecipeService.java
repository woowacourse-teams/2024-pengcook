package net.pengcook.ingredient.service;

import org.springframework.stereotype.Service;

import net.pengcook.ingredient.domain.Ingredient;
import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.repository.IngredientRecipeRepository;
import net.pengcook.recipe.domain.Recipe;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredientRecipeService {

    private final IngredientRecipeRepository ingredientRecipeRepository;

    public IngredientRecipe save(Ingredient ingredient, Recipe recipe, Requirement requirement) {
        IngredientRecipe ingredientRecipe = new IngredientRecipe(ingredient, recipe, requirement);
        return ingredientRecipeRepository.save(ingredientRecipe);
    }

}
