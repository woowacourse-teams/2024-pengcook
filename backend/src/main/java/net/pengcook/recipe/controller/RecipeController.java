package net.pengcook.recipe.controller;

import lombok.RequiredArgsConstructor;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.service.RecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/{id}")
    public RecipeResponse readRecipe(@PathVariable long id) {
        return recipeService.readRecipe(id);
    }
}
