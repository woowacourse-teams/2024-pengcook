package net.pengcook.ingredient.dto;

import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.ingredient.domain.Requirement;

public record IngredientResponse(long ingredientId, String ingredientName, Requirement requirement) {

    public IngredientResponse(IngredientRecipe ingredientRecipe) {
        this(
                ingredientRecipe.getIngredient().getId(),
                ingredientRecipe.getIngredient().getName(),
                ingredientRecipe.getRequirement()
        );
    }
}
