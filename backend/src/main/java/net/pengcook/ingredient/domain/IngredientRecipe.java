package net.pengcook.ingredient.domain;

import jakarta.persistence.*;

import net.pengcook.recipe.domain.Recipe;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class IngredientRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Enumerated(EnumType.STRING)
    private Requirement requirement;

    public IngredientRecipe(Ingredient ingredient, Recipe recipe, Requirement requirement) {
        this.ingredient = ingredient;
        this.recipe = recipe;
        this.requirement = requirement;
    }
}
