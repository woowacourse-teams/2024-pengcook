package net.pengcook.recipe.service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.pengcook.image.service.ImageClientService;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.domain.RecipeStep;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.dto.RecipeUpdateRequest;
import net.pengcook.recipe.exception.InvalidParameterException;
import net.pengcook.recipe.repository.RecipeStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeStepService {

    private final RecipeStepRepository recipeStepRepository;

    private final ImageClientService imageClientService;

    @Transactional(readOnly = true)
    public List<RecipeStepResponse> readRecipeSteps(long recipeId) {
        List<RecipeStep> recipeSteps = recipeStepRepository.findAllByRecipeIdOrderBySequence(recipeId);
        return convertToRecipeStepResponses(recipeSteps);
    }

    @Transactional
    public void saveRecipeSteps(Recipe recipe, List<RecipeStepRequest> recipeStepRequests) {
        recipeStepRequests.forEach(recipeStepRequest -> saveRecipeStep(recipe, recipeStepRequest));
    }

    @Transactional
    public void deleteRecipeStepsByRecipe(long recipeId) {
        recipeStepRepository.deleteByRecipeId(recipeId);
    }

    // TODO : 테스트
    @Transactional
    public void updateRecipeSteps(Recipe recipe, RecipeUpdateRequest recipeUpdateRequest) {
        // TODO : 레시피의 주인인지 여기서도 확인을 해야 할지도...?
        deleteRecipeStepsByRecipe(recipe.getId());
        recipeStepRepository.flush();
        saveRecipeSteps(recipe, recipeUpdateRequest.recipeSteps());
    }

    private void saveRecipeStep(Recipe savedRecipe, RecipeStepRequest recipeStepRequest) {
        String imageUrl = getValidatedImageUrl(recipeStepRequest.image());
        LocalTime cookingTime = getValidatedCookingTime(recipeStepRequest.cookingTime());

        RecipeStep recipeStep = new RecipeStep(
                savedRecipe,
                imageUrl,
                recipeStepRequest.description(),
                recipeStepRequest.sequence(),
                cookingTime
        );

        recipeStepRepository.save(recipeStep);
    }

    private List<RecipeStepResponse> convertToRecipeStepResponses(List<RecipeStep> recipeSteps) {
        return recipeSteps.stream()
                .map(RecipeStepResponse::new)
                .toList();
    }

    private String getValidatedImageUrl(String image) {
        if (image == null) {
            return null;
        }
        if (image.isBlank()) {
            throw new InvalidParameterException("적절하지 않은 이미지 이름입니다.");
        }
        return imageClientService.getImageUrl(image).url();
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
