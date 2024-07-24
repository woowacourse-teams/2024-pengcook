package net.pengcook.ingredient.service;


import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.pengcook.ingredient.domain.Ingredient;
import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.dto.IngredientCreateRequest;
import net.pengcook.ingredient.exception.InvalidNameException;
import net.pengcook.ingredient.repository.IngredientRepository;
import net.pengcook.recipe.domain.Recipe;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Getter
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientRecipeService ingredientRecipeService;
    private final IngredientSubstitutionService ingredientSubstitutionService;

    public void register(List<IngredientCreateRequest> requests, Recipe recipe) {
        validateDuplicateNames(getIngredientNames(requests));
        for (IngredientCreateRequest request : requests) {
            registerOne(request, recipe);
        }
    }

    private void registerOne(IngredientCreateRequest request, Recipe recipe) {
        Ingredient ingredient = registerOrGetIngredient(request.name());
        IngredientRecipe ingredientRecipe = registerIngredientRecipe(recipe, request, ingredient);

        if (request.requirement() == Requirement.ALTERNATIVE) {
            registerSubstitution(request, ingredientRecipe);
        }
    }

    private Ingredient registerOrGetIngredient(String ingredientName) {
        String name = ingredientName.toLowerCase();

        return ingredientRepository.findByName(name)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(name)));
    }

    private IngredientRecipe registerIngredientRecipe(
            Recipe recipe,
            IngredientCreateRequest request,
            Ingredient ingredient
    ) {
        return ingredientRecipeService.save(ingredient, recipe, request.requirement());
    }

    private void registerSubstitution(IngredientCreateRequest request, IngredientRecipe ingredientRecipe) {
        List<String> substitutionNames = request.substitutions();
        validateDuplicateNames(substitutionNames);

        for (String substitutionName : substitutionNames) {
            Ingredient substitution = registerOrGetIngredient(substitutionName);
            registerIngredientSubstitution(ingredientRecipe, substitution);
        }
    }

    private void registerIngredientSubstitution(IngredientRecipe ingredientRecipe, Ingredient substitution) {
        ingredientSubstitutionService.save(ingredientRecipe, substitution);
    }

    private void validateDuplicateNames(List<String> names) {
        if (hasDuplicateName(names)) {
            throw new InvalidNameException("ingredient name duplicated");
        }
    }

    private List<String> getIngredientNames(List<IngredientCreateRequest> requests) {
        return requests.stream()
                .map(IngredientCreateRequest::name)
                .map(String::toLowerCase)
                .toList();
    }

    private boolean hasDuplicateName(List<String> names) {
        HashSet<String> nonDuplicate = new HashSet<>(names);
        return (names.size() != nonDuplicate.size());
    }

}
