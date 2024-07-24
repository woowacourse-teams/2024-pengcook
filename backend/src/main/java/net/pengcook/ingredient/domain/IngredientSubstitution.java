package net.pengcook.ingredient.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class IngredientSubstitution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "ingredient_recipe_id")
    private IngredientRecipe ingredientRecipe;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    public IngredientSubstitution(IngredientRecipe ingredientRecipe, Ingredient ingredient) {
        this.ingredientRecipe = ingredientRecipe;
        this.ingredient = ingredient;
    }
}
