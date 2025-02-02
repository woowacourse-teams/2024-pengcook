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
            RecipeHomeResponse firstResponse
    ) {
        this(
                firstResponse.recipeId(),
                firstResponse.title(),
                new AuthorResponse(firstResponse.authorId(), firstResponse.authorName(), firstResponse.authorImage()),
                firstResponse.thumbnail(),
                firstResponse.likeCount(),
                firstResponse.commentCount(),
                firstResponse.createdAt(),
                userInfo.isSameUser(firstResponse.authorId())
        );
    }

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
