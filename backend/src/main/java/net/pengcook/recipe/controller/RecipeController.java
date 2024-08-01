package net.pengcook.recipe.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.category.dto.RecipeOfCategoryRequest;
import net.pengcook.recipe.dto.MainRecipeResponse;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.service.RecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public List<MainRecipeResponse> readRecipes(@RequestParam int pageNumber, @RequestParam int pageSize) {
        return recipeService.readRecipes(pageNumber, pageSize);
    }

    @PostMapping
    public RecipeResponse createRecipe(@LoginUser UserInfo userInfo, @RequestBody RecipeRequest recipeRequest) {
        return recipeService.createRecipe(userInfo, recipeRequest);
    }

    @GetMapping("/{id}/steps")
    public List<RecipeStepResponse> readRecipeSteps(@PathVariable long id) {
        return recipeService.readRecipeSteps(id);
    }

    @PostMapping("/{recipeId}/steps")
    public RecipeStepResponse createRecipeStep(@PathVariable long recipeId,
                                               @RequestBody RecipeStepRequest recipeStepRequest) {
        return recipeService.createRecipeStep(recipeId, recipeStepRequest);
    }

    @GetMapping("/search")
    public List<MainRecipeResponse> readRecipesOfCategory(@ModelAttribute RecipeOfCategoryRequest request) {
        return recipeService.readRecipesOfCategory(request);
    }
}
