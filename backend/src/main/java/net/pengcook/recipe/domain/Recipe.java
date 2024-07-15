package net.pengcook.recipe.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String thumbnail;
    private String category;
    private String requiredIngredient;
    private String optionalIngredient;
    private LocalTime cookingTime;
    private int difficulty;
    private int viewCount;
    private int likeCount;
    private String description;
    private String authorName;
    private String authorImage;
}
