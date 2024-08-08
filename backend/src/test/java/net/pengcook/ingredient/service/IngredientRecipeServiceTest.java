package net.pengcook.ingredient.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.repository.IngredientRecipeRepository;
import net.pengcook.ingredient.repository.IngredientSubstitutionRepository;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Import({
        IngredientService.class,
        IngredientRecipeService.class,
        IngredientSubstitutionService.class
})
@Sql(scripts = "/data/ingredient.sql")
class IngredientRecipeServiceTest {

    @Autowired
    private IngredientRecipeService ingredientRecipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRecipeRepository ingredientRecipeRepository;

    @Autowired
    private IngredientSubstitutionRepository ingredientSubstitutionRepository;

    @Test
    @DisplayName("레시피에 종속된 ingredientRecipe를 제거한다.")
    void deleteIngredientRecipe() {
        Recipe recipe = recipeRepository.findById(1L).get();
        int initialSize = ingredientRecipeRepository.findAll().size();
        int targetSize = extractIngredientRecipeSize(recipe);

        ingredientRecipeService.deleteIngredientRecipe(recipe);

        int expectedSize = ingredientRecipeRepository.findAll().size();
        assertThat(expectedSize).isEqualTo(initialSize - targetSize);
    }

    @Test
    @DisplayName("레시피에 종속된 ingredientSubstitution을 제거한다.")
    void deleteIngredientRecipeWithSubstitution() {
        Recipe recipe = recipeRepository.findById(1L).get();
        int initialSize = ingredientSubstitutionRepository.findAll().size();
        int targetSize = extractIngredientSubstitutionSize(recipe);

        ingredientRecipeService.deleteIngredientRecipe(recipe);

        int expectedSize = ingredientSubstitutionRepository.findAll().size();
        assertThat(expectedSize).isEqualTo(initialSize - targetSize);
    }

    private int extractIngredientRecipeSize(Recipe recipe) {
        List<IngredientRecipe> ingredientRecipes = ingredientRecipeRepository.findAllByRecipe(recipe);
        return ingredientRecipes.stream()
                .map((ingredientRecipe -> ingredientRecipe.getIngredient().getName()))
                .toList()
                .size();
    }

    private int extractIngredientSubstitutionSize(Recipe recipe) {
        List<IngredientRecipe> ingredientRecipes = ingredientRecipeRepository.findAllByRecipe(recipe);
        return ingredientRecipes.stream()
                .filter(ingredientRecipe -> ingredientRecipe.getRequirement() == Requirement.ALTERNATIVE)
                .mapToInt(
                        ingredientRecipe ->
                                ingredientSubstitutionRepository.findAllByIngredientRecipe(ingredientRecipe).size()
                )
                .sum();
    }
}
