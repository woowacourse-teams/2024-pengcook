package net.pengcook.recipe.dto;

import java.time.LocalDateTime;
import net.pengcook.block.domain.Ownable;

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
) implements Ownable {

    @Override
    public long getOwnerId() {
        return authorId;
    }
}
