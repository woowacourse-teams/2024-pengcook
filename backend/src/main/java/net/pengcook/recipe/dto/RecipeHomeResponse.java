package net.pengcook.recipe.dto;

import java.time.LocalDateTime;
import net.pengcook.user.domain.AuthorAble;

public record RecipeHomeResponse(
        long recipeId,
        String title,
        long authorId,
        String authorName,
        String authorImage,
        String thumbnail,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) implements AuthorAble {

    @Override
    public long getAuthorId() {
        return authorId;
    }
}
