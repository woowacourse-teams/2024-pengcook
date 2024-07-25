package net.pengcook.recipe.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.pengcook.recipe.domain.RecipeStep;
import net.pengcook.recipe.dto.AuthorResponse;
import net.pengcook.recipe.dto.CategoryResponse;
import net.pengcook.recipe.dto.IngredientResponse;
import net.pengcook.recipe.dto.MainRecipeResponse;
import net.pengcook.recipe.dto.RecipeDataResponse;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.recipe.repository.RecipeStepRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeStepRepository recipeStepRepository;

    public List<MainRecipeResponse> readRecipes(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Long> recipeIds = recipeRepository.findRecipeIds(pageable);

        List<RecipeDataResponse> recipeDataResponses = recipeRepository.findRecipeData(recipeIds);
        return convertToMainRecipeResponses(recipeDataResponses);
    }

    public List<RecipeStepResponse> readRecipeSteps(long id) {
        List<RecipeStep> recipeSteps = recipeStepRepository.findAllByRecipeIdOrderBySequence(id);
        return convertToRecipeStepResponses(recipeSteps);
    }

    private List<RecipeStepResponse> convertToRecipeStepResponses(List<RecipeStep> recipeSteps) {
        return recipeSteps.stream()
                .map(RecipeStepResponse::new)
                .toList();
    }

    public List<MainRecipeResponse> convertToMainRecipeResponses(List<RecipeDataResponse> recipeDataResponses) {
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
                firstResponse.description(),
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
}
