package net.pengcook.recipe.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pengcook.user.domain.User;

import java.time.LocalTime;

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

    @Column(nullable = false)
    private int commentCount;

    @Column(nullable = true)
    private String description;

    public Recipe(
            String title,
            User author,
            LocalTime cookingTime,
            String thumbnail,
            int difficulty,
            String description
    ) {
        this(0L, title, author, cookingTime, thumbnail, difficulty, 0, 0, description);
    }

    public void increaseLikeCount() {
        likeCount++;
    }

    public void decreaseLikeCount() {
        likeCount--;
    }
}
