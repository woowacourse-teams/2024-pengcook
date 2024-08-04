package net.pengcook.recipe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import net.pengcook.ingredient.dto.IngredientCreateRequest;

public record RecipeRequest(
        @NotBlank String title,
        @NotBlank String cookingTime,
        @NotBlank String thumbnail,
        @Min(0) @Max(10) int difficulty,
        @NotBlank String description,
        @NotEmpty List<String> categories,
        @NotEmpty List<IngredientCreateRequest> ingredients
) {
}
