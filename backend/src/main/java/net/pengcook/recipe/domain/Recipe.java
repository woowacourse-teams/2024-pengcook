package net.pengcook.recipe.domain;

import jakarta.persistence.Column;
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
import net.pengcook.user.domain.User;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private LocalTime cookingTime;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private int difficulty;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = true)
    private String description;

    public Recipe(
            String title,
            User author,
            LocalTime cookingTime,
            String thumbnail,
            int difficulty,
            int likeCount,
            String description
    ) {
        this(0L, title, author, cookingTime, thumbnail, difficulty, likeCount, description);
    }
}
