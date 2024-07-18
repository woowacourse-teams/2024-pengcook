package net.pengcook.recipe.dto;

public record HomeRecipeRequest(
        int pageNumber,
        int numberOfElements
) {
}
