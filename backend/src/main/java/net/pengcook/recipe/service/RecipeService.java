package net.pengcook.recipe.service;

import java.time.LocalTime;
import java.util.Collection;
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
import net.pengcook.recipe.dto.RecipeHomeWithMineResponseV1;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.exception.UnauthorizedException;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private static final String KEY_RECIPE = "id";

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

    @Transactional(readOnly = true)
    public List<RecipeHomeWithMineResponse> readRecipes(UserInfo userInfo, PageRecipeRequest pageRecipeRequest) {
        Pageable pageable = pageRecipeRequest.getPageable();
        List<Long> recipeIds = recipeRepository.findRecipeIdsByCategoryAndKeyword(
                pageable,
                pageRecipeRequest.category(),
                pageRecipeRequest.keyword(),
                pageRecipeRequest.userId()
        );

        List<RecipeDataResponse> recipeDataResponses = recipeRepository.findRecipeData(recipeIds);
        return convertToMainRecipeResponses(userInfo, recipeDataResponses);
    }

    @Transactional(readOnly = true)
    public List<RecipeHomeWithMineResponseV1> readRecipesV1(UserInfo userInfo, PageRecipeRequest pageRecipeRequest) {
        List<Long> recipeIds = findRecipeIdsByMultipleCondition(pageRecipeRequest);
        List<RecipeHomeResponse> recipeHomeResponses = recipeRepository.findRecipeDataV1(recipeIds);

        return recipeHomeResponses.stream()
                .map(recipeHomeResponse -> new RecipeHomeWithMineResponseV1(userInfo, recipeHomeResponse))
                .sorted(Comparator.comparing(RecipeHomeWithMineResponseV1::recipeId).reversed())
                .toList();
    }

    private List<Long> findRecipeIdsByMultipleCondition(PageRecipeRequest pageRecipeRequest) {
        Pageable pageable = pageRecipeRequest.getPageable();
        long conditionCount = pageRecipeRequest.getConditionCount();

        if (conditionCount == 0) {
            Pageable descPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(KEY_RECIPE).descending()
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
            return recipeRepository.findRecipeByAuthorIdOrderByIdDesc(pageable, userId).stream()
                    .map(Recipe::getId)
                    .toList();
        }
        // TODO: need to throw illegal state
        return List.of();
    }


    @Transactional(readOnly = true)
    public List<RecipeHomeWithMineResponse> readLikeRecipes(UserInfo userInfo) {
        List<Long> likeRecipeIds = likeRepository.findRecipeIdsByUserId(userInfo.getId());
        List<RecipeDataResponse> recipeDataResponses = recipeRepository.findRecipeData(likeRecipeIds);

        return convertToMainRecipeResponses(userInfo, recipeDataResponses);
    }

    @Transactional(readOnly = true)
    public List<RecipeHomeWithMineResponseV1> readLikeRecipesV1(UserInfo userInfo) {
        List<Long> likeRecipeIds = likeRepository.findRecipeIdsByUserId(userInfo.getId());
        List<RecipeHomeResponse> recipeHomeResponses = recipeRepository.findRecipeDataV1(likeRecipeIds);

        return recipeHomeResponses.stream()
                .map(recipeHomeResponse -> new RecipeHomeWithMineResponseV1(userInfo, recipeHomeResponse))
                .sorted(Comparator.comparing(RecipeHomeWithMineResponseV1::recipeId).reversed())
                .toList();
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public RecipeDescriptionResponse readRecipeDescription(UserInfo userInfo, long recipeId) {
        List<RecipeDataResponse> recipeDataResponses = recipeRepository.findRecipeData(recipeId);

        return new RecipeDescriptionResponse(
                userInfo,
                recipeDataResponses.getFirst(),
                getCategoryResponses(recipeDataResponses),
                getIngredientResponses(recipeDataResponses)
        );
    }

    @Transactional
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

    private List<RecipeHomeWithMineResponse> convertToMainRecipeResponses(
            UserInfo userInfo,
            List<RecipeDataResponse> recipeDataResponses
    ) {
        Collection<List<RecipeDataResponse>> groupedRecipeData = recipeDataResponses.stream()
                .collect(Collectors.groupingBy(RecipeDataResponse::recipeId))
                .values();

        return groupedRecipeData.stream()
                .map(data -> getMainRecipeResponse(userInfo, data))
                .sorted(Comparator.comparing(RecipeHomeWithMineResponse::recipeId).reversed())
                .collect(Collectors.toList());
    }

    private RecipeHomeWithMineResponse getMainRecipeResponse(UserInfo userInfo,
                                                             List<RecipeDataResponse> groupedResponses) {
        RecipeDataResponse firstResponse = groupedResponses.getFirst();

        return new RecipeHomeWithMineResponse(
                userInfo,
                firstResponse,
                getCategoryResponses(groupedResponses),
                getIngredientResponses(groupedResponses)
        );
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
