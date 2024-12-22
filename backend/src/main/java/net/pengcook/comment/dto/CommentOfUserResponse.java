package net.pengcook.comment.dto;

import java.time.LocalDateTime;
import net.pengcook.comment.domain.Comment;

public record CommentOfUserResponse(
        long commentId,
        long recipeId,
        String recipeTitle,
        String recipeThumbnail,
        LocalDateTime createdAt,
        String message) {

    public CommentOfUserResponse(Comment comment) {
        this(
                comment.getId(),
                comment.getRecipe().getId(),
                comment.getRecipe().getTitle(),
                comment.getRecipe().getThumbnail(),
                comment.getCreatedAt(),
                comment.getMessage());
    }
}
