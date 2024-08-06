package net.pengcook.recipe.dto;

import java.time.LocalTime;
import net.pengcook.recipe.domain.RecipeStep;

public record RecipeStepResponse(
        long id,
        long recipeId,
        String image,
        String description,
        int sequence,
        LocalTime cookingTime) {

    public RecipeStepResponse(RecipeStep recipeStep) {
        this(
                recipeStep.getId(),
                recipeStep.recipeId(),
                recipeStep.getImage(),
                recipeStep.getDescription(),
                recipeStep.getSequence(),
                recipeStep.getCookingTime()
        );
    }
}
