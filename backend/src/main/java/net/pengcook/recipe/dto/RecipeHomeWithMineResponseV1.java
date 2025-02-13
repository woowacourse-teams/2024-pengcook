package net.pengcook.recipe.dto;

import java.time.LocalDateTime;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.recipe.domain.Recipe;

public record RecipeHomeWithMineResponseV1(
        long recipeId,
        String title,
        AuthorResponse author,
        String thumbnail,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt,
        boolean mine
) {

    public RecipeHomeWithMineResponseV1(
            UserInfo userInfo,
            Recipe recipe
    ) {
        this(
                recipe.getId(),
                recipe.getTitle(),
                new AuthorResponse(recipe.getAuthor()),
                recipe.getThumbnail(),
                recipe.getLikeCount(),
                recipe.getCommentCount(),
                recipe.getCreatedAt(),
                userInfo.isSameUser(recipe.getAuthor().getId())
        );
    }
}
