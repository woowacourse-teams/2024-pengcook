package net.pengcook.recipe.dto;

import jakarta.validation.constraints.Min;

public record RecipeOfUserRequest(long userId, @Min(0) int pageNumber, @Min(1) int pageSize) {
}
