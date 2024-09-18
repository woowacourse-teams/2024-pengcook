package net.pengcook.recipe.service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.pengcook.image.service.S3ClientService;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.domain.RecipeStep;
import net.pengcook.recipe.dto.RecipeStepRequest;
import net.pengcook.recipe.dto.RecipeStepResponse;
import net.pengcook.recipe.exception.InvalidParameterException;
import net.pengcook.recipe.exception.NotFoundException;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.recipe.repository.RecipeStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeStepService {

    private final RecipeStepRepository recipeStepRepository;
    private final RecipeRepository recipeRepository;
    private final S3ClientService s3ClientService;

    @Transactional(readOnly = true)
    public List<RecipeStepResponse> readRecipeSteps(long recipeId) {
        List<RecipeStep> recipeSteps = recipeStepRepository.findAllByRecipeIdOrderBySequence(recipeId);
        return convertToRecipeStepResponses(recipeSteps);
    }

    @Transactional
    public void saveRecipeSteps(Long savedRecipeId, List<RecipeStepRequest> recipeStepRequests) {
        Recipe savedRecipe = recipeRepository.findById(savedRecipeId)
                .orElseThrow(() -> new NotFoundException("해당되는 레시피가 없습니다."));
        recipeStepRequests.forEach(recipeStepRequest -> saveRecipeStep(savedRecipe, recipeStepRequest));
    }

    public void deleteRecipeStepsByRecipe(long recipeId) {
        recipeStepRepository.deleteByRecipeId(recipeId);
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
