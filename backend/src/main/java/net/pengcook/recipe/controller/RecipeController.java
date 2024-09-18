package net.pengcook.recipe.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.recipe.dto.MainRecipeResponse;
import net.pengcook.recipe.dto.PageRecipeRequest;
import net.pengcook.recipe.dto.RecipeDescriptionResponse;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.service.RecipeService;
import net.pengcook.recipe.service.RecipeStepService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public List<MainRecipeResponse> readRecipes(
            @LoginUser UserInfo userInfo,
            @ModelAttribute @Valid PageRecipeRequest pageRecipeRequest
    ) {
        return recipeService.readRecipes(userInfo, pageRecipeRequest);
    }

    @GetMapping("/likes")
    public List<MainRecipeResponse> readLikeRecipes(@LoginUser UserInfo userInfo) {
        return recipeService.readLikeRecipes(userInfo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse createRecipe(@LoginUser UserInfo userInfo, @RequestBody @Valid RecipeRequest recipeRequest) {
        return recipeService.createRecipe(userInfo, recipeRequest);
    }

    @GetMapping("/{recipeId}")
    public RecipeDescriptionResponse readRecipeDescription(@LoginUser UserInfo userInfo, @PathVariable long recipeId) {
        return recipeService.readRecipeDescription(userInfo, recipeId);
    }

    @DeleteMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@LoginUser UserInfo userInfo, @PathVariable long recipeId) {
        recipeService.deleteRecipe(userInfo, recipeId);
    }

    @GetMapping("/{recipeId}/steps")
    public List<RecipeStepResponse> readRecipeSteps(@PathVariable long recipeId) {
        return recipeStepService.readRecipeSteps(recipeId);
    }
}
