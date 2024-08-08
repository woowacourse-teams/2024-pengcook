package net.pengcook.recipe.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    private String image;

    private String description;

    private int sequence;

    private LocalTime cookingTime;

    public RecipeStep(Recipe recipe, String image, String description, int sequence, LocalTime cookingTime) {
        this(0L, recipe, image, description, sequence, cookingTime);
    }

    public long recipeId() {
        return recipe.getId();
    }

    public RecipeStep update(String imageUrl, String description, LocalTime cookingTime) {
        this.image = imageUrl;
        this.description = description;
        this.cookingTime = cookingTime;

        return this;
    }
}
