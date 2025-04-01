package net.pengcook.recipe.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.recipe.dto.PageRecipeRequest;
import net.pengcook.recipe.dto.RecipeDescriptionResponse;
import net.pengcook.recipe.dto.RecipeHomeWithMineResponse;
import net.pengcook.recipe.dto.RecipeHomeWithMineResponseV1;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.dto.RecipeUpdateRequest;
import net.pengcook.recipe.service.RecipeService;
import net.pengcook.recipe.service.RecipeStepService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeStepService recipeStepService;

    @GetMapping("/recipes")
    public List<RecipeHomeWithMineResponse> readRecipes(
            @LoginUser UserInfo userInfo,
            @ModelAttribute @Valid PageRecipeRequest pageRecipeRequest
    ) {
        return recipeService.readRecipes(userInfo, pageRecipeRequest);
    }

    @GetMapping(value = "/recipes", produces = "application/vnd.pengcook.v1+json")
    public List<RecipeHomeWithMineResponseV1> readRecipesV1(
            @LoginUser UserInfo userInfo,
            @ModelAttribute @Valid PageRecipeRequest pageRecipeRequest
    ) {
        return recipeService.readRecipesV1(userInfo, pageRecipeRequest);
    }

    // TODO : 배포 후 삭제
    @GetMapping("/recipes/likes")
    public List<RecipeHomeWithMineResponse> readLikeRecipesOld(@LoginUser UserInfo userInfo) {
        return recipeService.readLikeRecipes(userInfo);
    }

    @GetMapping("/users/me/likes/recipes")
    public List<RecipeHomeWithMineResponse> readLikeRecipes(@LoginUser UserInfo userInfo) {
        return recipeService.readLikeRecipes(userInfo);
    }

    // TODO : 배포 후 삭제
    @GetMapping(value = "/recipes/likes", produces = "application/vnd.pengcook.v1+json")
    public List<RecipeHomeWithMineResponseV1> readLikeRecipesV1Old(@LoginUser UserInfo userInfo) {
        return recipeService.readLikeRecipesV1(userInfo);
    }

    @GetMapping(value = "/users/me/likes/recipes", produces = "application/vnd.pengcook.v1+json")
    public List<RecipeHomeWithMineResponseV1> readLikeRecipesV1(@LoginUser UserInfo userInfo) {
        return recipeService.readLikeRecipesV1(userInfo);
    }

    // TODO : 배포 후 삭제
    @GetMapping("/recipes/follows")
    public List<RecipeHomeWithMineResponseV1> readFollowRecipesOld(
            @LoginUser UserInfo userInfo,
            @ModelAttribute @Valid PageRecipeRequest pageRecipeRequest
    ) {
        return recipeService.readFollowRecipes(userInfo, pageRecipeRequest);
    }

    @GetMapping("/users/me/followings/recipes")
    public List<RecipeHomeWithMineResponseV1> readFollowRecipes(
            @LoginUser UserInfo userInfo,
            @ModelAttribute @Valid PageRecipeRequest pageRecipeRequest
    ) {
        return recipeService.readFollowRecipes(userInfo, pageRecipeRequest);
    }

    @PostMapping("/recipes")
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse createRecipe(@LoginUser UserInfo userInfo, @RequestBody @Valid RecipeRequest recipeRequest) {
        return recipeService.createRecipe(userInfo, recipeRequest);
    }

    @PutMapping("/recipes/{recipeId}")
    public void updateRecipe(
            @LoginUser UserInfo userInfo,
            @PathVariable Long recipeId,
            @RequestBody @Valid RecipeUpdateRequest recipeUpdateRequest
    ) {
        recipeService.updateRecipe(userInfo, recipeId, recipeUpdateRequest);
    }

    @GetMapping("/recipes/{recipeId}")
    public RecipeDescriptionResponse readRecipeDescription(@LoginUser UserInfo userInfo, @PathVariable long recipeId) {
        return recipeService.readRecipeDescription(userInfo, recipeId);
    }

    @DeleteMapping("/recipes/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@LoginUser UserInfo userInfo, @PathVariable long recipeId) {
        recipeService.deleteRecipe(userInfo, recipeId);
    }

    @GetMapping("/recipes/{recipeId}/steps")
    public List<RecipeStepResponse> readRecipeSteps(@PathVariable long recipeId) {
        return recipeStepService.readRecipeSteps(recipeId);
    }
}
