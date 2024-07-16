package net.pengcook.recipe.service;

import lombok.RequiredArgsConstructor;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.dto.HomeRecipeRequest;
import net.pengcook.recipe.dto.HomeRecipeResponse;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.repository.RecipeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public Page<HomeRecipeResponse> readHomeRecipes(HomeRecipeRequest request) {
        PageRequest pageable = PageRequest.of(request.pageNumber(), request.numberOfElements());
        Page<Recipe> recipes = recipeRepository.findAll(pageable);
        return recipes.map(HomeRecipeResponse::new);
    }

    public RecipeResponse readRecipe(long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow();
        return new RecipeResponse(recipe);
    }
}
