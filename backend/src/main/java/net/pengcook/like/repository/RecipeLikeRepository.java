package net.pengcook.like.repository;

import net.pengcook.like.domain.RecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    Optional<RecipeLike> findByUserIdAndRecipeId(long userId, long recipeId);
}
