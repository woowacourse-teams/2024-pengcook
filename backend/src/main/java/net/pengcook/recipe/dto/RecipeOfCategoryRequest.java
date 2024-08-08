package net.pengcook.recipe.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RecipeOfCategoryRequest(@NotBlank String category, @Min(0) int pageNumber, @Min(1) int pageSize) {
}
