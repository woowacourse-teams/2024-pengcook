package net.pengcook.recipe.service;

import lombok.RequiredArgsConstructor;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.repository.RecipeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeResponse readRecipe(long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow();
        return new RecipeResponse(recipe);
    }
}
