package net.pengcook.recipe.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.user.domain.Ownable;

public record RecipeDataResponse(
        long recipeId,
        String title,
        long authorId,
        String authorName,
        String authorImage,
        LocalTime cookingTime,
        String thumbnail,
        int difficulty,
        int likeCount,
        int commentCount,
        String description,
        LocalDateTime createdAt,
        long categoryId,
        String categoryName,
        long ingredientId,
        String ingredientName,
        Requirement ingredientRequirement
) implements Ownable {

    @Override
    public long getOwnerId() {
        return authorId;
    }
}
