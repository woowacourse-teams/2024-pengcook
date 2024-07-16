package net.pengcook.recipe.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.recipe.dto.HomeRecipeRequest;
import net.pengcook.recipe.dto.HomeRecipeResponse;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.service.RecipeService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/{id}")
    @GetMapping("/api/home")
    public Page<HomeRecipeResponse> readHomeRecipes(@RequestBody HomeRecipeRequest request) {
        return recipeService.readHomeRecipes(request);
    }

    @GetMapping("/api/recipe/{id}")
    public RecipeResponse readRecipe(@PathVariable long id) {
        return recipeService.readRecipe(id);
    }
}
