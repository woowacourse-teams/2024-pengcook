package net.pengcook.recipe.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pengcook.category.domain.CategoryRecipe;
import net.pengcook.ingredient.domain.IngredientRecipe;
import net.pengcook.user.domain.User;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "recipe")
    private List<CategoryRecipe> category;

    @OneToMany(mappedBy = "recipe")
    private List<IngredientRecipe> ingredient;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private LocalTime cookingTime;

    private String thumbnail;

    private int difficulty;

    private int likeCount;

    private String description;
}
