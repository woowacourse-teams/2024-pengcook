package net.pengcook.recipe.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import net.pengcook.authentication.domain.UserInfo;

public record RecipeHomeWithMineResponse(
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

    public RecipeHomeWithMineResponse(
            UserInfo userInfo,
            RecipeDataResponse firstResponse,
            List<CategoryResponse> category,
            List<IngredientResponse> ingredient

    ) {
        this(
                firstResponse.recipeId(),
                firstResponse.title(),
                new AuthorResponse(firstResponse.authorId(), firstResponse.authorName(), firstResponse.authorImage()),
                firstResponse.cookingTime(),
                firstResponse.thumbnail(),
                firstResponse.difficulty(),
                firstResponse.likeCount(),
                firstResponse.commentCount(),
                firstResponse.description(),
                firstResponse.createdAt(),
                category,
                ingredient,
                userInfo.isSameUser(firstResponse.authorId())
        );
    }
}
