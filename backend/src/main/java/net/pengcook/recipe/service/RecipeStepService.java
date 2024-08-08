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
@Transactional
@RequiredArgsConstructor
public class RecipeStepService {

    private final RecipeStepRepository recipeStepRepository;
    private final RecipeRepository recipeRepository;
    private final S3ClientService s3ClientService;

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

    private List<RecipeStepResponse> convertToRecipeStepResponses(List<RecipeStep> recipeSteps) {
        return recipeSteps.stream()
                .map(RecipeStepResponse::new)
                .toList();
    }

    private void validateRecipeStepSequence(long recipeId, int sequence) {
        int previousSequence = sequence - 1;
        if (previousSequence >= 1) {
            recipeStepRepository.findByRecipeIdAndSequence(recipeId, previousSequence)
                    .orElseThrow(() -> new InvalidParameterException("이전 스텝이 등록되지 않았습니다."));
        }
    }

    private Recipe getRecipeByRecipeId(long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("해당되는 레시피가 없습니다."));
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

    private RecipeStep saveRecipeStep(
            Recipe recipe,
            String imageUrl,
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
}
