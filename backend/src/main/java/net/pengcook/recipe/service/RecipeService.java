package net.pengcook.recipe.service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.category.dto.RecipeOfCategoryRequest;
import net.pengcook.category.repository.CategoryRecipeRepository;
import net.pengcook.category.service.CategoryService;
import net.pengcook.image.service.S3ClientService;
import net.pengcook.ingredient.service.IngredientService;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.domain.RecipeStep;
import net.pengcook.recipe.dto.AuthorResponse;
import net.pengcook.recipe.dto.CategoryResponse;
import net.pengcook.recipe.dto.IngredientResponse;
import net.pengcook.recipe.dto.MainRecipeResponse;
import net.pengcook.recipe.dto.PageRecipeRequest;
import net.pengcook.recipe.dto.RecipeDataResponse;
import net.pengcook.recipe.dto.RecipeOfUserRequest;
import net.pengcook.recipe.dto.RecipeRequest;
import net.pengcook.recipe.dto.RecipeResponse;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.exception.InvalidParameterException;
import net.pengcook.recipe.exception.NotFoundException;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.recipe.repository.RecipeStepRepository;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final CategoryRecipeRepository categoryRecipeRepository;
    private final UserRepository userRepository;

    private final CategoryService categoryService;
    private final IngredientService ingredientService;
    private final S3ClientService s3ClientService;

    public List<MainRecipeResponse> readRecipes(PageRecipeRequest pageRecipeRequest) {
        Pageable pageable = getPageable(pageRecipeRequest.pageNumber(), pageRecipeRequest.pageSize());
        List<Long> recipeIds = recipeRepository.findRecipeIds(pageable);

        List<RecipeDataResponse> recipeDataResponses = recipeRepository.findRecipeData(recipeIds);
        return convertToMainRecipeResponses(recipeDataResponses);
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

        return new RecipeResponse(savedRecipe);
    }

    public List<RecipeStepResponse> readRecipeSteps(long recipeId) {
        List<RecipeStep> recipeSteps = recipeStepRepository.findAllByRecipeIdOrderBySequence(recipeId);
        return convertToRecipeStepResponses(recipeSteps);
    }

    public RecipeStepResponse readRecipeStep(long recipeId, long sequence) {
        RecipeStep recipeStep = recipeStepRepository.findByRecipeIdAndSequence(recipeId, sequence)
                .orElseThrow(() -> new NotFoundException("해당되는 레시피 스텝 정보가 없습니다."));

        return new RecipeStepResponse(recipeStep);
    }

    public RecipeStepResponse createRecipeStep(long recipeId, RecipeStepRequest recipeStepRequest) {
        validateRecipeStepSequence(recipeId, recipeStepRequest.sequence());
        Recipe recipe = getRecipeByRecipeId(recipeId);
        String imageUrl = getValidatedImageUrl(recipeStepRequest.image());
        String description = recipeStepRequest.description();
        LocalTime cookingTime = getValidatedCookingTime(recipeStepRequest.cookingTime());

        Optional<RecipeStep> existingRecipeStep = recipeStepRepository.findByRecipeIdAndSequence(
                recipeId,
                recipeStepRequest.sequence()
        );

        RecipeStep recipeStep = existingRecipeStep
                .map(currentRecipeStep -> currentRecipeStep.update(imageUrl, description, cookingTime))
                .orElseGet(() -> saveRecipeStep(recipe, imageUrl, recipeStepRequest, cookingTime));

        return new RecipeStepResponse(recipeStep);
    }

    public List<MainRecipeResponse> readRecipesOfCategory(RecipeOfCategoryRequest request) {
        String categoryName = request.category();
        Pageable pageable = getPageable(request.pageNumber(), request.pageSize());
        List<Long> recipeIds = categoryRecipeRepository.findRecipeIdsByCategoryName(categoryName, pageable);

        List<RecipeDataResponse> recipeDataResponses = recipeRepository.findRecipeData(recipeIds);
        return convertToMainRecipeResponses(recipeDataResponses);
    }

    public List<MainRecipeResponse> readRecipesOfUser(RecipeOfUserRequest request) {
        long userId = request.userId();
        Pageable pageable = getPageable(request.pageNumber(), request.pageSize());
        List<Long> recipeIds = recipeRepository.findRecipeIdsByUserId(userId, pageable);

        List<RecipeDataResponse> recipeDataResponses = recipeRepository.findRecipeData(recipeIds);
        return convertToMainRecipeResponses(recipeDataResponses);
    }

    private List<RecipeStepResponse> convertToRecipeStepResponses(List<RecipeStep> recipeSteps) {
        return recipeSteps.stream()
                .map(RecipeStepResponse::new)
                .toList();
    }

    private List<MainRecipeResponse> convertToMainRecipeResponses(List<RecipeDataResponse> recipeDataResponses) {
        Collection<List<RecipeDataResponse>> groupedRecipeData = recipeDataResponses.stream()
                .collect(Collectors.groupingBy(RecipeDataResponse::recipeId))
                .values();

        return groupedRecipeData.stream()
                .map(this::getMainRecipeResponse)
                .sorted(Comparator.comparing(MainRecipeResponse::recipeId).reversed())
                .collect(Collectors.toList());
    }

    private MainRecipeResponse getMainRecipeResponse(List<RecipeDataResponse> groupedResponses) {
        RecipeDataResponse firstResponse = groupedResponses.getFirst();

        return new MainRecipeResponse(
                firstResponse.recipeId(),
                firstResponse.title(),
                new AuthorResponse(firstResponse.authorId(), firstResponse.authorName(), firstResponse.authorImage()),
                firstResponse.cookingTime(),
                firstResponse.thumbnail(),
                firstResponse.difficulty(),
                firstResponse.likeCount(),
                firstResponse.commentCount(),
                firstResponse.description(),
                firstResponse.createdAt(),
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

    private Pageable getPageable(int pageNumber, int pageSize) {
        long offset = (long) pageNumber * pageSize;
        if (offset > Integer.MAX_VALUE) {
            throw new InvalidParameterException("적절하지 않은 페이지 정보입니다.");
        }
        return PageRequest.of(pageNumber, pageSize);
    }

    private Recipe getRecipeByRecipeId(long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("해당되는 레시피가 없습니다."));
    }

    private void validateRecipeStepSequence(long recipeId, int sequence) {
        int previousSequence = sequence - 1;
        if (previousSequence >= 1) {
            recipeStepRepository.findByRecipeIdAndSequence(recipeId, previousSequence)
                    .orElseThrow(() -> new InvalidParameterException("이전 스텝이 등록되지 않았습니다."));
        }
    }

    private RecipeStep saveRecipeStep(
            Recipe recipe, String imageUrl,
            RecipeStepRequest recipeStepRequest,
            LocalTime cookingTime
    ) {
        RecipeStep recipeStep = new RecipeStep(
                recipe,
                imageUrl,
                recipeStepRequest.description(),
                recipeStepRequest.sequence(),
                cookingTime
        );

        return recipeStepRepository.save(recipeStep);
    }

    private String getValidatedImageUrl(String image) {
        if (image == null) {
            return null;
        }
        if (image.isBlank()) {
            throw new InvalidParameterException("적절하지 않은 이미지 이름입니다.");
        }
        return s3ClientService.getImageUrl(image).url();
    }

    private LocalTime getValidatedCookingTime(String cookingTime) {
        try {
            return Optional.ofNullable(cookingTime)
                    .map(LocalTime::parse)
                    .orElse(null);
        } catch (DateTimeParseException exception) {
            throw new InvalidParameterException("적절하지 않은 조리시간입니다.");
        }
    }
}
