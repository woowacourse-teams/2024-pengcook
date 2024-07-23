package net.pengcook.recipe.dto;

import net.pengcook.ingredient.domain.Requirement;

public record IngredientResponse(long ingredientId, String ingredientName, Requirement requirement) {
}
