package net.pengcook.recipe.dto;

import java.time.LocalTime;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.user.domain.AuthorAble;

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
        String description,
        long categoryId,
        String categoryName,
        long ingredientId,
        String ingredientName,
        Requirement ingredientRequirement
) implements AuthorAble {

    @Override
    public long getAuthorId() {
        return authorId;
    }
}
