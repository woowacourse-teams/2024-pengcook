package net.pengcook.like.repository;

import java.util.List;
import net.pengcook.like.domain.RecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    @Query("""
            SELECT r.recipe.id FROM RecipeLike r
            WHERE r.user.id = :userId
            """)
    List<Long> findRecipeIdsByUserId(long userId);

    boolean existsByUserIdAndRecipeId(long userId, long recipeId);

    void deleteByUserIdAndRecipeId(long userId, long recipeId);

    void deleteByRecipeId(long recipeId);

    void deleteByUserId(long userId);
}
