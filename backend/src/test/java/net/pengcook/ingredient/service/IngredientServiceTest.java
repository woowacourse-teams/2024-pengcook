package net.pengcook.ingredient.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import net.pengcook.ingredient.domain.Ingredient;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.dto.IngredientCreateRequest;
import net.pengcook.ingredient.exception.InvalidNameException;
import net.pengcook.ingredient.repository.IngredientRecipeRepository;
import net.pengcook.ingredient.repository.IngredientRepository;
import net.pengcook.ingredient.repository.IngredientSubstitutionRepository;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.repository.RecipeRepository;
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
    private IngredientRecipeRepository ingredientRecipeRepository;

    @Autowired
    private IngredientSubstitutionRepository ingredientSubstitutionRepository;

    private Recipe recipe;

    private List<IngredientCreateRequest> requests;

    @BeforeEach
    void setUp() {
        recipe = recipeRepository.findById(1L).get();
    }

    @Test
    @DisplayName("재료 등록 시, 중복된 재료 이름이 요청으로 들어온다면 예외가 발생한다.")
    void registerWhenRequestContainsDuplicatedIngredientName() {
        requests = List.of(
                new IngredientCreateRequest("Kimchi", Requirement.REQUIRED, null),
                new IngredientCreateRequest("Kimchi", Requirement.REQUIRED, null)
        );

        assertThatThrownBy(() -> ingredientService.register(requests, recipe))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    @DisplayName("재료 등록 시, 중복된 대체 재료 이름이 요청으로 들어온다면 예외가 발생한다.")
    void registerWhenRequestContainsDuplicatedSubstitutionName() {
        requests = List.of(
                new IngredientCreateRequest("beef", Requirement.ALTERNATIVE, List.of("chicken", "chicken"))
        );

        assertThatThrownBy(() -> ingredientService.register(requests, recipe))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    @DisplayName("재료 등록 시, Ingredient와 IngredientRecipe에 데이터를 등록한다.")
    void register() {
        requests = List.of(
                new IngredientCreateRequest("Rice", Requirement.REQUIRED, null),
                new IngredientCreateRequest("cheese", Requirement.REQUIRED, null),
                new IngredientCreateRequest("Kimchi", Requirement.REQUIRED, null),
                new IngredientCreateRequest("pork", Requirement.ALTERNATIVE, List.of("beef", "chicken", "fish"))
        );

        ingredientService.register(requests, recipe);

        List<String> ingredientNames = ingredientRepository.findAll().stream()
                .map(Ingredient::getName)
                .toList();
        List<String> ingredientRecipeNames = ingredientRecipeRepository.findAll().stream()
                .map(ingredientRecipe -> ingredientRecipe.getIngredient().getName())
                .toList();

        assertThat(ingredientNames).usingRecursiveComparison()
                .isEqualTo(List.of("rice", "cheese", "kimchi", "pork", "beef", "chicken", "fish"));
        assertThat(ingredientRecipeNames).usingRecursiveComparison()
                .isEqualTo(List.of("rice", "cheese", "kimchi", "pork"));
    }

    @Test
    @DisplayName("재료 등록 시, Requirement가 ALTERNATIVE라면 IngredientSubstitution에 데이터를 등록한다.")
    void registerSubstitution() {
        requests = List.of(
                new IngredientCreateRequest("Rice", Requirement.REQUIRED, null),
                new IngredientCreateRequest("cheese", Requirement.REQUIRED, null),
                new IngredientCreateRequest("Kimchi", Requirement.REQUIRED, null),
                new IngredientCreateRequest("pork", Requirement.ALTERNATIVE, List.of("beef", "chicken", "fish"))
        );

        ingredientService.register(requests, recipe);

        List<String> substitutionNames = ingredientSubstitutionRepository.findAll().stream()
                .map(ingredientSubstitution -> ingredientSubstitution.getIngredient().getName())
                .toList();

        assertThat(substitutionNames).usingRecursiveComparison()
                .isEqualTo(List.of("beef", "chicken", "fish"));
    }
}
