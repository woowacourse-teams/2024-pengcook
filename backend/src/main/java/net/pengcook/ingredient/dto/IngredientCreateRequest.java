package net.pengcook.ingredient.dto;

import java.util.List;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.exception.InvalidNameException;

public record IngredientCreateRequest(
        String name,
        Requirement requirement,
        List<String> substitutions
) {

    public IngredientCreateRequest {
        validateName(name);
        if (requirement == Requirement.ALTERNATIVE) {
            validateSubstitutions(substitutions);
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidNameException("name cannot be null or empty");
        }
    }

    private void validateSubstitutions(List<String> substitutions) {
        if (substitutions == null) {
            throw new InvalidNameException("substitutions cannot be null");
        }
        for (String substitution : substitutions) {
            validateName(substitution);
        }
    }

}
