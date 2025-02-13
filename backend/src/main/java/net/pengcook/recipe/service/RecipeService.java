package net.pengcook.recipe.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.category.dto.CategoryResponse;
import net.pengcook.category.service.CategoryService;
import net.pengcook.comment.service.CommentService;
import net.pengcook.image.service.ImageClientService;
import net.pengcook.ingredient.dto.IngredientResponse;
import net.pengcook.ingredient.service.IngredientRecipeService;
import net.pengcook.ingredient.service.IngredientService;
import net.pengcook.like.repository.RecipeLikeRepository;
import net.pengcook.like.service.RecipeLikeService;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.dto.PageRecipeRequest;
import net.pengcook.recipe.dto.RecipeDescriptionResponse;
import net.pengcook.recipe.dto.RecipeHomeWithMineResponse;
import net.pengcook.recipe.dto.RecipeHomeWithMineResponseV1;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.dto.RecipeUpdateRequest;
import net.pengcook.recipe.exception.NotFoundException;
import net.pengcook.recipe.exception.UnauthorizedException;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.recipe.repository.RecipeStepRepository;
import net.pengcook.user.domain.User;
import net.pengcook.user.domain.UserFollow;
import net.pengcook.user.repository.UserFollowRepository;
import net.pengcook.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private static final String CREATION_DATE = "createdAt";

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeLikeRepository likeRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final UserFollowRepository userFollowRepository;

    private final CategoryService categoryService;
    private final IngredientService ingredientService;
    private final ImageClientService imageClientService;
    private final RecipeStepService recipeStepService;
    private final IngredientRecipeService ingredientRecipeService;
    private final CommentService commentService;
    private final RecipeLikeService recipeLikeService;

    @Transactional(readOnly = true)
    public List<RecipeHomeWithMineResponse> readRecipes(UserInfo userInfo, PageRecipeRequest pageRecipeRequest) {
        Pageable pageable = pageRecipeRequest.getPageable();
        List<Long> recipeIds = recipeRepository.findRecipeIdsByCategoryAndKeyword(
                pageable,
                pageRecipeRequest.category(),
                pageRecipeRequest.keyword(),
                pageRecipeRequest.userId()
        );

        return getRecipeHomeWithMineResponses(userInfo, recipeIds);
    }

    @Transactional(readOnly = true)
    public List<RecipeHomeWithMineResponseV1> readRecipesV1(UserInfo userInfo, PageRecipeRequest pageRecipeRequest) {
        List<Long> recipeIds = findRecipeIdsByMultipleCondition(pageRecipeRequest);
        List<Recipe> recipes = recipeRepository.findAllByIdInOrderByCreatedAtDesc(recipeIds);

        return recipes.stream()
                .map(recipe -> new RecipeHomeWithMineResponseV1(userInfo, recipe))
                .toList();
    }

    private List<Long> findRecipeIdsByMultipleCondition(PageRecipeRequest pageRecipeRequest) {
        Pageable pageable = pageRecipeRequest.getPageable();
        long conditionCount = pageRecipeRequest.getConditionCount();

        if (conditionCount == 0) {
            Pageable descPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(CREATION_DATE).descending()
            );
            return recipeRepository.findAll(descPageable).stream()
                    .map(Recipe::getId)
                    .toList();
        }
        if (conditionCount == 1) {
            return findRecipeIdsBySingleCondition(pageRecipeRequest);
        }

        return recipeRepository.findRecipeIdsByCategoryAndKeyword(
                pageable,
                pageRecipeRequest.category(),
                pageRecipeRequest.keyword(),
                pageRecipeRequest.userId()
        );
    }

    private List<Long> findRecipeIdsBySingleCondition(PageRecipeRequest pageRecipeRequest) {
        Pageable pageable = pageRecipeRequest.getPageable();
        String category = pageRecipeRequest.category();
        String keyword = pageRecipeRequest.keyword();
        Long userId = pageRecipeRequest.userId();

        if (category != null) {
            return recipeRepository.findRecipeIdsByCategory(pageable, category);
        }
        if (keyword != null) {
            return recipeRepository.findRecipeIdsByKeyword(pageable, keyword);
        }
        if (userId != null) {
            return recipeRepository.findRecipeByAuthorIdOrderByCreatedAtDesc(pageable, userId).stream()
                    .map(Recipe::getId)
                    .toList();
        }
        // TODO: need to throw illegal state
        return List.of();
    }

    @Transactional(readOnly = true)
    public List<RecipeHomeWithMineResponse> readLikeRecipes(UserInfo userInfo) {
        List<Long> likeRecipeIds = likeRepository.findRecipeIdsByUserId(userInfo.getId());

        return getRecipeHomeWithMineResponses(userInfo, likeRecipeIds);
    }

    @Transactional(readOnly = true)
    public List<RecipeHomeWithMineResponseV1> readLikeRecipesV1(UserInfo userInfo) {
        List<Long> likeRecipeIds = likeRepository.findRecipeIdsByUserId(userInfo.getId());
        List<Recipe> recipes = recipeRepository.findAllByIdInOrderByCreatedAtDesc(likeRecipeIds);

        return recipes.stream()
                .map(recipe -> new RecipeHomeWithMineResponseV1(userInfo, recipe))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecipeHomeWithMineResponseV1> readFollowRecipes(UserInfo userInfo,
                                                                PageRecipeRequest pageRecipeRequest) {
        List<UserFollow> followings = userFollowRepository.findAllByFollowerId(userInfo.getId());
        List<Long> followeeIds = followings.stream()
                .map(userFollow -> userFollow.getFollowee().getId())
                .toList();
        List<Recipe> recipes = recipeRepository.findAllByAuthorIdInOrderByCreatedAtDesc(followeeIds,
                pageRecipeRequest.getPageable());

        return recipes.stream()
                .map(recipe -> new RecipeHomeWithMineResponseV1(userInfo, recipe))
                .toList();
    }


    @Transactional
    public RecipeResponse createRecipe(UserInfo userInfo, RecipeRequest recipeRequest) {
        User author = userRepository.findById(userInfo.getId()).orElseThrow();
        String thumbnailUrl = imageClientService.getImageUrl(recipeRequest.thumbnail()).url();
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

    @Transactional
    public void updateRecipe(UserInfo userInfo, Long recipeId, RecipeUpdateRequest recipeUpdateRequest) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        verifyRecipeOwner(userInfo, recipe);

        Recipe updatedRecipe = recipe.updateRecipe(
                recipeUpdateRequest.title(),
                LocalTime.parse(recipeUpdateRequest.cookingTime()),
                imageClientService.getImageUrl(recipeUpdateRequest.thumbnail()).url(),
                recipeUpdateRequest.difficulty(),
                recipeUpdateRequest.description()
        );

        ingredientRecipeService.deleteIngredientRecipe(recipe.getId());
        ingredientService.register(recipeUpdateRequest.ingredients(), updatedRecipe);
        categoryService.deleteCategoryRecipe(recipe);
        categoryService.saveCategories(updatedRecipe, recipeUpdateRequest.categories());

        recipeStepService.deleteRecipeStepsByRecipe(updatedRecipe.getId());
        recipeStepRepository.flush();
        recipeStepService.saveRecipeSteps(updatedRecipe.getId(), recipeUpdateRequest.recipeSteps());
    }

    @Transactional(readOnly = true)
    public RecipeDescriptionResponse readRecipeDescription(UserInfo userInfo, long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 레시피입니다."));
        List<CategoryResponse> categories = categoryService.findCategoryByRecipe(recipe);
        List<IngredientResponse> ingredients = ingredientService.findIngredientByRecipe(recipe);
        boolean isLike = likeRepository.existsByUserIdAndRecipeId(userInfo.getId(), recipeId);

        return new RecipeDescriptionResponse(userInfo, recipe, categories, ingredients, isLike);
    }

    @Transactional
    public void deleteRecipe(UserInfo userInfo, long recipeId) {
        Optional<Recipe> targetRecipe = recipeRepository.findById(recipeId);

        targetRecipe.ifPresent(recipe -> deleteRecipe(userInfo, recipe));
    }

    @Transactional
    public void deleteRecipe(UserInfo userInfo, Recipe recipe) {
        verifyRecipeOwner(userInfo, recipe);
        ingredientRecipeService.deleteIngredientRecipe(recipe.getId());
        categoryService.deleteCategoryRecipe(recipe);
        commentService.deleteCommentsByRecipe(recipe.getId());
        recipeLikeService.deleteLikesByRecipe(recipe.getId());
        recipeStepService.deleteRecipeStepsByRecipe(recipe.getId());
        recipeRepository.delete(recipe);
    }

    private List<RecipeHomeWithMineResponse> getRecipeHomeWithMineResponses(UserInfo userInfo, List<Long> recipeIds) {
        List<Recipe> recipes = recipeRepository.findAllByIdInOrderByCreatedAtDesc(recipeIds);
        return recipes.stream()
                .map(recipe -> {
                    List<CategoryResponse> categories = categoryService.findCategoryByRecipe(recipe);
                    List<IngredientResponse> ingredients = ingredientService.findIngredientByRecipe(recipe);
                    return new RecipeHomeWithMineResponse(userInfo, recipe, categories, ingredients);
                })
                .toList();
    }

    private void verifyRecipeOwner(UserInfo userInfo, Recipe recipe) {
        User author = recipe.getAuthor();
        long authorId = author.getId();
        if (!userInfo.isSameUser(authorId)) {
            throw new UnauthorizedException("레시피에 대한 권한이 없습니다.");
        }
    }
}
