package net.pengcook.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RecipeStepRequest(
        String image,
        @NotBlank String description,
        @Positive int sequence,
        @NotBlank String cookingTime
) {
}
