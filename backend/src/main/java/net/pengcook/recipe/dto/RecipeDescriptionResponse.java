package net.pengcook.recipe.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.category.dto.CategoryResponse;
import net.pengcook.ingredient.dto.IngredientResponse;
import net.pengcook.recipe.domain.Recipe;

public record RecipeDescriptionResponse(
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
        boolean mine,
        boolean isLike
) {

    public RecipeDescriptionResponse(
            UserInfo userInfo,
            Recipe recipe,
            List<CategoryResponse> category,
            List<IngredientResponse> ingredient,
            boolean isLike
    ) {
        this(
                recipe.getId(),
                recipe.getTitle(),
                new AuthorResponse(recipe.getAuthor()),
                recipe.getCookingTime(),
                recipe.getThumbnail(),
                recipe.getDifficulty(),
                recipe.getLikeCount(),
                recipe.getCommentCount(),
                recipe.getDescription(),
                recipe.getCreatedAt(),
                category,
                ingredient,
                userInfo.isSameUser(recipe.getAuthor().getId()),
                isLike
        );
    }
}
