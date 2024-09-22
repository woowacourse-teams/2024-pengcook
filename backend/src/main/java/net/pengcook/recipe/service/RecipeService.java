package net.pengcook.recipe.service;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.category.service.CategoryService;
import net.pengcook.comment.service.CommentService;
import net.pengcook.image.service.S3ClientService;
import net.pengcook.ingredient.service.IngredientRecipeService;
import net.pengcook.ingredient.service.IngredientService;
import net.pengcook.like.repository.RecipeLikeRepository;
import net.pengcook.like.service.RecipeLikeService;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.dto.CategoryResponse;
import net.pengcook.recipe.dto.IngredientResponse;
import net.pengcook.recipe.dto.PageRecipeRequest;
import net.pengcook.recipe.dto.RecipeDataResponse;
import net.pengcook.recipe.dto.RecipeDescriptionResponse;
import net.pengcook.recipe.dto.RecipeHomeResponse;
import net.pengcook.recipe.dto.RecipeHomeWithMineResponse;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.exception.UnauthorizedException;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeLikeRepository likeRepository;

    private final CategoryService categoryService;
    private final IngredientService ingredientService;
    private final S3ClientService s3ClientService;
    private final RecipeStepService recipeStepService;
    private final IngredientRecipeService ingredientRecipeService;
    private final CommentService commentService;
    private final RecipeLikeService recipeLikeService;

    public List<RecipeHomeWithMineResponse> readRecipes(UserInfo userInfo, PageRecipeRequest pageRecipeRequest) {
        Pageable pageable = pageRecipeRequest.getPageable();
        List<Long> recipeIds = recipeRepository.findRecipeIdsByCategoryAndKeyword(
                pageable,
                pageRecipeRequest.category(),
                pageRecipeRequest.keyword(),
                pageRecipeRequest.userId()
        );

        List<RecipeHomeResponse> recipeHomeResponses = recipeRepository.findRecipeData(recipeIds);

        return recipeHomeResponses.stream()
                .map(recipeHomeResponse -> new RecipeHomeWithMineResponse(userInfo, recipeHomeResponse))
                .sorted(Comparator.comparing(RecipeHomeWithMineResponse::recipeId).reversed())
                .toList();
    }

    public List<RecipeHomeWithMineResponse> readLikeRecipes(UserInfo userInfo) {
        List<Long> likeRecipeIds = likeRepository.findRecipeIdsByUserId(userInfo.getId());
        List<RecipeHomeResponse> recipeHomeResponses = recipeRepository.findRecipeData(likeRecipeIds);

        return recipeHomeResponses.stream()
                .map(recipeHomeResponse -> new RecipeHomeWithMineResponse(userInfo, recipeHomeResponse))
                .sorted(Comparator.comparing(RecipeHomeWithMineResponse::recipeId).reversed())
                .toList();
    }

    public RecipeResponse createRecipe(UserInfo userInfo, RecipeRequest recipeRequest) {
        User author = userRepository.findById(userInfo.getId()).orElseThrow();
        String thumbnailUrl = s3ClientService.getImageUrl(recipeRequest.thumbnail()).url();
        Recipe recipe = new Recipe(
                recipeRequest.title(),
                author,
                LocalTime.parse(recipeRequest.cookingTime()),
                thumbnailUrl,
                recipeRequest.difficulty(),
                recipeRequest.description()
        );

        Recipe savedRecipe = recipeRepository.save(recipe);
        categoryService.saveCategories(savedRecipe, recipeRequest.categories());
        ingredientService.register(recipeRequest.ingredients(), savedRecipe);
        recipeStepService.saveRecipeSteps(savedRecipe.getId(), recipeRequest.recipeSteps());

        return new RecipeResponse(savedRecipe);
    }

    public RecipeDescriptionResponse readRecipeDescription(UserInfo userInfo, long recipeId) {
        List<RecipeDataResponse> recipeDataResponses = recipeRepository.findRecipeData(recipeId);

        return new RecipeDescriptionResponse(
                userInfo,
                recipeDataResponses.getFirst(),
                getCategoryResponses(recipeDataResponses),
                getIngredientResponses(recipeDataResponses)
        );
    }

    public void deleteRecipe(UserInfo userInfo, long recipeId) {
        Optional<Recipe> targetRecipe = recipeRepository.findById(recipeId);

        targetRecipe.ifPresent(recipe -> {
            verifyUserCanDeleteRecipe(userInfo, recipe);
            ingredientRecipeService.deleteIngredientRecipe(recipe.getId());
            categoryService.deleteCategoryRecipe(recipe);
            commentService.deleteCommentsByRecipe(recipe.getId());
            recipeLikeService.deleteLikesByRecipe(recipe.getId());
            recipeStepService.deleteRecipeStepsByRecipe(recipe.getId());
            recipeRepository.delete(recipe);
        });
    }

    private List<IngredientResponse> getIngredientResponses(List<RecipeDataResponse> groupedResponses) {
        return groupedResponses.stream()
                .map(r -> new IngredientResponse(r.ingredientId(), r.ingredientName(), r.ingredientRequirement()))
                .distinct()
                .collect(Collectors.toList());
    }

    private List<CategoryResponse> getCategoryResponses(List<RecipeDataResponse> groupedResponses) {
        return groupedResponses.stream()
                .map(r -> new CategoryResponse(r.categoryId(), r.categoryName()))
                .distinct()
                .collect(Collectors.toList());
    }

    private void verifyUserCanDeleteRecipe(UserInfo userInfo, Recipe recipe) {
        if (recipe.getAuthor().getId() != userInfo.getId()) {
            throw new UnauthorizedException("레시피를 삭제할 수 없습니다.");
        }
    }
}
