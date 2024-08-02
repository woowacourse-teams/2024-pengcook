package net.pengcook.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import net.pengcook.ingredient.dto.IngredientCreateRequest;

public record RecipeRequest(
        @NotBlank String title,
        @NotBlank String cookingTime,
        @NotBlank String thumbnail,
        @NotBlank int difficulty,
        @NotBlank String description,
        @NotBlank List<String> categories,
        @NotBlank List<IngredientCreateRequest> ingredients
) {
}
