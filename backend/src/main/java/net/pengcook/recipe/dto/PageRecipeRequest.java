package net.pengcook.recipe.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;

public record PageRecipeRequest(
        @Min(0) int pageNumber,
        @Min(1) int pageSize,
        @Nullable String category,
        @Nullable String keyword,
        @Nullable Long userId
) {
}
