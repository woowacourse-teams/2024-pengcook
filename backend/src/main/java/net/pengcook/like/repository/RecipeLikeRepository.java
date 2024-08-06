package net.pengcook.like.repository;

import java.util.Optional;
import net.pengcook.like.domain.RecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    Optional<RecipeLike> findByUserIdAndRecipeId(long userId, long recipeId);

    void deleteByUserIdAndRecipeId(long userId, long recipeId);
}
