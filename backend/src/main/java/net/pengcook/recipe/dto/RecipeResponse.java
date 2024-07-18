package net.pengcook.recipe.dto;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import net.pengcook.recipe.domain.Recipe;

public record RecipeResponse(
        String title,
        String thumbnail,
        String authorImage,
        String authorName,
        List<String> category,
        List<String> requiredIngredient,
        List<String> optionalIngredient,
        int difficulty,
        LocalTime cookingTime,
        int likeCount,
        int viewCount,
        String description
) {

    public RecipeResponse(Recipe recipe) {
        this(
                recipe.getTitle(),
                recipe.getThumbnail(),
                recipe.getAuthorImage(),
                recipe.getAuthorName(),
                Arrays.stream(recipe.getCategory().split(",")).toList(),
                Arrays.stream(recipe.getRequiredIngredient().split(",")).toList(),
                Arrays.stream(recipe.getOptionalIngredient().split(",")).toList(),
                recipe.getDifficulty(),
                recipe.getCookingTime(),
                recipe.getLikeCount(),
                recipe.getViewCount(),
                recipe.getDescription()
        );
    }
}
