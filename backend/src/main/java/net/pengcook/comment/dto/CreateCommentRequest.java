package net.pengcook.comment.dto;

public record CreateCommentRequest(long recipeId, String message) {
}
