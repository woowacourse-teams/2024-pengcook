package net.pengcook.recipe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.time.Duration;
import java.util.List;
import net.pengcook.ingredient.dto.IngredientCreateRequest;

public record RecipeRequest(
        @NotBlank String title,
        @NotBlank @Pattern(regexp = "^\\d+:\\d+:\\d+$") String cookingTime,
        @NotBlank String thumbnail,
        @Min(0) @Max(10) int difficulty,
        @NotBlank String description,
        @NotEmpty List<String> categories,
        @NotEmpty List<IngredientCreateRequest> ingredients,
        List<RecipeStepRequest> recipeSteps
) {
    public Duration parseCookingTime() {
        String[] parts = cookingTime.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        if (seconds >= 60) {
            minutes += seconds / 60;
            seconds %= 60;
        }

        if (minutes >= 60) {
            hours += minutes / 60;
            minutes %= 60;
        }

        return Duration.ofHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);
    }
}
