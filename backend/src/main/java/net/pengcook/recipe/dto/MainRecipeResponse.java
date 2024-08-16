package net.pengcook.recipe.dto;

import java.time.LocalDateTime;
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
        int commentCount,
        String description,
        LocalDateTime createdAt,
        List<CategoryResponse> category,
        List<IngredientResponse> ingredient,
        boolean mine
) {
}
