package net.pengcook.recipe.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PageRecipeRequest(
        @Nullable @NotBlank String category,
        @Nullable @NotBlank String keyword,
        @Min(0) int pageNumber,
        @Min(1) int pageSize
) {
    public PageRecipeRequest(@Min(0) int pageNumber, @Min(1) int pageSize) {
        this(null, null, pageNumber, pageSize);
    }
}
