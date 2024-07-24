package net.pengcook.recipe.dto;

import net.pengcook.recipe.domain.RecipeStep;

public record RecipeStepResponse(long id, long recipeId, String image, String description, int sequence) {

    public RecipeStepResponse(RecipeStep recipeStep) {
        this(
                recipeStep.getId(),
                recipeStep.recipeId(),
                recipeStep.getImage(),
                recipeStep.getDescription(),
                recipeStep.getSequence()
        );
    }
}
