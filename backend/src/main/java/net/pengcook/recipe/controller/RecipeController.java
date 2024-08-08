package net.pengcook.recipe.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.recipe.dto.MainRecipeResponse;
import net.pengcook.recipe.dto.PageRecipeRequest;
import net.pengcook.recipe.dto.RecipeOfCategoryRequest;
import net.pengcook.recipe.dto.RecipeOfUserRequest;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.service.RecipeService;
import net.pengcook.recipe.service.RecipeStepService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeStepService recipeStepService;

    @GetMapping
    public List<MainRecipeResponse> readRecipes(@ModelAttribute @Valid PageRecipeRequest pageRecipeRequest) {
        return recipeService.readRecipes(pageRecipeRequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse createRecipe(@LoginUser UserInfo userInfo, @RequestBody @Valid RecipeRequest recipeRequest) {
        return recipeService.createRecipe(userInfo, recipeRequest);
    }

    @GetMapping("/search")
    public List<MainRecipeResponse> readRecipesOfCategory(
            @ModelAttribute @Valid RecipeOfCategoryRequest recipeOfCategoryRequest
    ) {
        return recipeService.readRecipesOfCategory(recipeOfCategoryRequest);
    }

    @GetMapping("/search/user")
    public List<MainRecipeResponse> readRecipesOfUser(
            @ModelAttribute @Valid RecipeOfUserRequest recipeOfUserRequest
    ) {
        return recipeService.readRecipesOfUser(recipeOfUserRequest);
    }

    @GetMapping("/{recipeId}/steps")
    public List<RecipeStepResponse> readRecipeSteps(@PathVariable long recipeId) {
        return recipeStepService.readRecipeSteps(recipeId);
    }

    @GetMapping("/{recipeId}/steps/{sequence}")
    public RecipeStepResponse readRecipeStep(@PathVariable long recipeId, @PathVariable long sequence) {
        return recipeStepService.readRecipeStep(recipeId, sequence);
    }

    @PostMapping("/{recipeId}/steps")
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeStepResponse createRecipeStep(
            @PathVariable long recipeId,
            @RequestBody @Valid RecipeStepRequest recipeStepRequest
    ) {
        return recipeStepService.createRecipeStep(recipeId, recipeStepRequest);
    }
}
