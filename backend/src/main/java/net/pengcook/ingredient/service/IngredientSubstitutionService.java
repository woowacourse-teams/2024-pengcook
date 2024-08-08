package net.pengcook.ingredient.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.ingredient.domain.Ingredient;
import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.ingredient.domain.IngredientSubstitution;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.repository.IngredientSubstitutionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientSubstitutionService {

    private final IngredientSubstitutionRepository ingredientSubstitutionRepository;

    public void save(IngredientRecipe ingredientRecipe, Ingredient substitution) {
        IngredientSubstitution ingredientSubstitution = new IngredientSubstitution(ingredientRecipe, substitution);
        ingredientSubstitutionRepository.save(ingredientSubstitution);
    }

    public void delete(IngredientRecipe ingredientRecipe) {
        if (ingredientRecipe.getRequirement() == Requirement.ALTERNATIVE) {
            List<IngredientSubstitution> substitutions =
                    ingredientSubstitutionRepository.findAllByIngredientRecipe(ingredientRecipe);
            ingredientSubstitutionRepository.deleteAll(substitutions);
        }
    }
}
