package net.pengcook.recipe.dto;

import java.time.LocalTime;

public record RecipeStepRequest(String image, String description, int sequence, LocalTime cookingTime) {
}
