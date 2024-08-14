package net.pengcook.like.repository;

import net.pengcook.like.domain.RecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    boolean existsByUserIdAndRecipeId(long userId, long recipeId);

    void deleteByUserIdAndRecipeId(long userId, long recipeId);

    void deleteByRecipeId(long recipeId);

    void deleteByUserId(long userId);
}
