package net.pengcook.recipe.repository;

import jakarta.annotation.Nullable;
import java.util.List;
import net.pengcook.block.repository.OwnableRepository;
import net.pengcook.recipe.domain.Recipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends OwnableRepository<Recipe, Long> {

    @Query("""
            SELECT recipe
            FROM CategoryRecipe
            WHERE category.name = :category
            ORDER BY recipe.createdAt DESC
            """)
    List<Recipe> findAllByCategory(Pageable pageable, String category);

    @Query("""
            SELECT r
            FROM Recipe r
            WHERE r.title LIKE CONCAT('%', :keyword, '%')
            OR r.description LIKE CONCAT('%', :keyword, '%')
            ORDER BY r.createdAt DESC
            """)
    List<Recipe> findAllByKeyword(Pageable pageable, String keyword);

    List<Recipe> findAllByAuthorIdOrderByCreatedAtDesc(Pageable pageable, long authorId);

    @Query("""
            SELECT r
            FROM Recipe r
            LEFT JOIN CategoryRecipe cr ON cr.recipe = r
            LEFT JOIN Category c ON cr.category = c
            WHERE (:category IS NULL OR c.name = :category)
            AND (:keyword IS NULL OR CONCAT(r.title, r.description) LIKE CONCAT('%', :keyword, '%'))
            AND (:userId IS NULL OR r.author.id = :userId)
            GROUP BY r.id, r.createdAt
            ORDER BY r.createdAt DESC
            """)
    List<Recipe> findAllByCategoryAndKeyword(
            Pageable pageable,
            @Param("category") @Nullable String category,
            @Param("keyword") @Nullable String keyword,
            @Param("userId") @Nullable Long userId
    );

    List<Recipe> findAllByIdInOrderByCreatedAtDesc(List<Long> recipeIds);

    List<Recipe> findAllByAuthorId(long authorId);

    List<Recipe> findAllByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds, Pageable pageable);

    int countByAuthorId(long userId);
}
