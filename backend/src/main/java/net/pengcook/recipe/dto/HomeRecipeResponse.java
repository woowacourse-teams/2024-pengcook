package net.pengcook.recipe.dto;

import net.pengcook.recipe.domain.Recipe;

public record HomeRecipeResponse(
        long recipeId,
        String title,
        String thumbnail,
        String authorImage,
        String authorName
) {

    public HomeRecipeResponse(Recipe recipe) {
        this(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getThumbnail(),
                recipe.getAuthorImage(),
                recipe.getAuthorName()
        );
    }
}
