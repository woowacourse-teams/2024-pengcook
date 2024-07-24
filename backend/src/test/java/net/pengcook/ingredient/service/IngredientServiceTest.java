package net.pengcook.ingredient.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import java.util.List;
import net.pengcook.ingredient.domain.Ingredient;
import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.ingredient.domain.IngredientSubstitution;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.dto.IngredientCreateRequest;
import net.pengcook.ingredient.exception.InvalidNameException;
import net.pengcook.ingredient.repository.IngredientRecipeRepository;
import net.pengcook.ingredient.repository.IngredientRepository;
import net.pengcook.ingredient.repository.IngredientSubstitutionRepository;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
class IngredientServiceTest {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IngredientRecipeRepository ingredientRecipeRepository;

    private Recipe saved;

    private List<IngredientCreateRequest> requests;
    @Autowired
    private IngredientSubstitutionRepository ingredientSubstitutionRepository;

    @BeforeEach
    void setUp() {
        User author = userRepository.findById(1L).get();
        Recipe recipe = new Recipe(
                0L,
                "Delicious Pancakes",
                author,
                LocalTime.of(0, 30),
                "food.jpg",
                2,
                10,
                "A simple and delicious pancake recipe."
        );

        saved = recipeRepository.save(recipe);
        requests = List.of(
                new IngredientCreateRequest("Rice", Requirement.REQUIRED, null),
                new IngredientCreateRequest("cheese", Requirement.REQUIRED, null),
                new IngredientCreateRequest("Kimchi", Requirement.REQUIRED, null),
                new IngredientCreateRequest("pork", Requirement.ALTERNATIVE, List.of("beef", "chicken", "fish"))
        );
    }

    @Test
    @DisplayName("재료 이름 중복 검증")
    void validateDuplicatedIngredientName() {
        requests = List.of(
                new IngredientCreateRequest("Kimchi", Requirement.REQUIRED, null),
                new IngredientCreateRequest("Kimchi", Requirement.REQUIRED, null)
        );

        assertThatThrownBy(() -> ingredientService.register(requests, saved))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    @DisplayName("대체 재료 이름 중복 검증")
    void validateDuplicatedSubstitutionName() {
        requests = List.of(
                new IngredientCreateRequest("beef", Requirement.ALTERNATIVE, List.of("chicken", "chicken"))
        );

        assertThatThrownBy(() -> ingredientService.register(requests, saved))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    @DisplayName("재료 등록")
    void registerFromIngredientCreateRequest() {
        ingredientService.register(requests, saved);

        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<IngredientRecipe> ingredientRecipes = ingredientRecipeRepository.findAll();
        List<String> ingredientNames = ingredients.stream()
                .map(Ingredient::getName)
                .toList();
        List<String> ingredientRecipeNames = ingredientRecipes.stream()
                .map(ingredientRecipe -> ingredientRecipe.getIngredient().getName())
                .toList();

        assertThat(ingredientNames).usingRecursiveComparison()
                .isEqualTo(List.of("rice", "cheese", "kimchi", "pork", "beef", "chicken", "fish"));
        assertThat(ingredientRecipeNames).usingRecursiveComparison()
                .isEqualTo(List.of("rice", "cheese", "kimchi", "pork"));

    }

    @Test
    @DisplayName("대체 재료 등록")
    void registerSubstitution() {
        ingredientService.register(requests, saved);

        List<IngredientSubstitution> substitutions = ingredientSubstitutionRepository.findAll();
        List<String> substitutionNames = substitutions.stream()
                .map(ingredientSubstitution -> ingredientSubstitution.getIngredient().getName())
                .toList();

        assertThat(substitutionNames).usingRecursiveComparison()
                .isEqualTo(List.of("beef", "chicken", "fish"));
    }
}
