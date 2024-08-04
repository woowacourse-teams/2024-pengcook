package net.pengcook.comment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(@Min(1) long recipeId, @NotBlank String message) {
}
