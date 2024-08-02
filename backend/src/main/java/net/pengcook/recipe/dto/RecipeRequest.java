package net.pengcook.recipe.dto;

import java.util.List;
import net.pengcook.ingredient.dto.IngredientCreateRequest;

public record RecipeRequest(
        String title,
        String cookingTime,
        String thumbnail,
        int difficulty,
        String description,
        List<String> categories,
        List<IngredientCreateRequest> ingredients
) {
}
