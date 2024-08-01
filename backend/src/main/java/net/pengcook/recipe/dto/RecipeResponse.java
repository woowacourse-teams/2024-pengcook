package net.pengcook.recipe.dto;

import net.pengcook.recipe.domain.Recipe;

public record RecipeResponse(long recipeId) {

    public RecipeResponse(Recipe savedRecipe) {
        this(savedRecipe.getId());
    }
}
