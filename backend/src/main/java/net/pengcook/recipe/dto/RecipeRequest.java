package net.pengcook.recipe.dto;

import jakarta.annotation.Nullable;
import java.time.LocalTime;
import java.util.List;
import net.pengcook.ingredient.dto.IngredientCreateRequest;

public record RecipeRequest(
        String title,
        LocalTime cookingTime,
        String thumbnail,
        int difficulty,
        int likeCount,
        String description,
        List<String> categories,
        List<IngredientCreateRequest> ingredients
) {
}
