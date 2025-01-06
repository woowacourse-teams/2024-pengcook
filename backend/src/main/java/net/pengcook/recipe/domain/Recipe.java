package net.pengcook.recipe.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pengcook.user.domain.Ownable;
import net.pengcook.user.domain.User;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Recipe implements Ownable {

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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Recipe(
            String title,
            User author,
            LocalTime cookingTime,
            String thumbnail,
            int difficulty,
            String description
    ) {
        this(0L, title, author, cookingTime, thumbnail, difficulty, 0, 0, description, LocalDateTime.now());
    }

    public Recipe updateRecipe(
            String title,
            LocalTime cookingTime,
            String thumbnail,
            int difficulty,
            String description
    ) {
        this.title = title;
        this.cookingTime = cookingTime;
        this.thumbnail = thumbnail;
        this.difficulty = difficulty;
        this.description = description;
        return this;

    }

    public void increaseLikeCount() {
        likeCount++;
    }

    public void decreaseLikeCount() {
        likeCount--;
    }

    public void increaseCommentCount() {
        commentCount++;
    }

    public void decreaseCommentCount() {
        commentCount--;
    }

    @Override
    public long getOwnerId() {
        return author.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Recipe recipe = (Recipe) o;
        return Objects.equals(id, recipe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
