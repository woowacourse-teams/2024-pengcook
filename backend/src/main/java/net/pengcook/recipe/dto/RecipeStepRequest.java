package net.pengcook.recipe.dto;

public record RecipeStepRequest(String image, String description, int sequence, String cookingTime) {
}
