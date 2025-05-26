package net.pengcook.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(long recipeId, @NotBlank String message) {
}
