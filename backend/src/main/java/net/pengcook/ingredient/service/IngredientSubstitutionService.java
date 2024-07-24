package net.pengcook.ingredient.service;

import org.springframework.stereotype.Service;

import net.pengcook.ingredient.domain.Ingredient;
import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.ingredient.domain.IngredientSubstitution;
import net.pengcook.ingredient.repository.IngredientSubstitutionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredientSubstitutionService {

    private final IngredientSubstitutionRepository ingredientSubstitutionRepository;

    public void save(IngredientRecipe ingredientRecipe, Ingredient substitution) {
        IngredientSubstitution ingredientSubstitution = new IngredientSubstitution(ingredientRecipe, substitution);
        ingredientSubstitutionRepository.save(ingredientSubstitution);
    }
}
