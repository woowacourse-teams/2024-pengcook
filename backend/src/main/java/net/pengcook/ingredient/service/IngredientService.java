package net.pengcook.ingredient.service;

import static net.pengcook.ingredient.domain.Requirement.ALTERNATIVE;

import java.util.HashSet;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import net.pengcook.ingredient.domain.Ingredient;
import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.ingredient.dto.IngredientCreateRequest;
import net.pengcook.ingredient.repository.IngredientRepository;
import net.pengcook.recipe.domain.Recipe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
        Ingredient ingredient = registerIngredient(request.name());
        IngredientRecipe ingredientRecipe = registerIngredientRecipe(recipe, request, ingredient);

        if (request.requirement() == ALTERNATIVE) {
            registerSubstitution(request, ingredientRecipe);
        }
    }

    private Ingredient registerIngredient(String ingredientName) {
        String name = ingredientName.toLowerCase();

        if (ingredientRepository.existsByName(name)) {
            return ingredientRepository.findByName(name);
        }

        return ingredientRepository.save(new Ingredient(name));
    }

    private IngredientRecipe registerIngredientRecipe(Recipe recipe, IngredientCreateRequest request, Ingredient ingredient) {
        return ingredientRecipeService.save(ingredient, recipe, request.requirement());
    }

    private void registerSubstitution(IngredientCreateRequest request, IngredientRecipe ingredientRecipe) {
        List<String> substitutionNames = request.substitutions();
        validateDuplicateNames(substitutionNames);

        for (String substitutionName : substitutionNames) {
            Ingredient substitution = registerIngredient(substitutionName);
            registerIngredientSubstitution(ingredientRecipe, substitution);
        }
    }

    private void registerIngredientSubstitution(IngredientRecipe ingredientRecipe, Ingredient substitution) {
        ingredientSubstitutionService.save(ingredientRecipe, substitution);
    }

    private void validateDuplicateNames(List<String> names) {
        if (hasDuplicateName(names)) {
            throw new IllegalArgumentException("ingredient name duplicated");
        }
    }

    private List<String> getIngredientNames(List<IngredientCreateRequest> requests) {
        return requests.stream()
                .map((IngredientCreateRequest::name))
                .map(String::toLowerCase)
                .toList();
    }

    private boolean hasDuplicateName(List<String> names) {
        HashSet<String> nonDuplicate = new HashSet<>(names);
        return (names.size() != nonDuplicate.size());
    }

}
