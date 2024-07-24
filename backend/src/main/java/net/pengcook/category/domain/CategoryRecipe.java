package net.pengcook.category.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.pengcook.recipe.domain.Recipe;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public CategoryRecipe(Category category, Recipe recipe) {
        this(0L, category, recipe);
    }
}
