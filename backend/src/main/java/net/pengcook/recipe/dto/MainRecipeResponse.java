package net.pengcook.recipe.dto;

import java.time.LocalTime;
import java.util.List;

public record MainRecipeResponse(
        long recipeId,
        String title,
        AuthorResponse author,
        LocalTime cookingTime,
        String thumbnail,
        int difficulty,
        int likeCount,
        String description,
        List<CategoryResponse> category,
        List<IngredientResponse> ingredient
) {
}
