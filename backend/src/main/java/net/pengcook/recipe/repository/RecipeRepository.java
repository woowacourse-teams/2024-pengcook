package net.pengcook.recipe.repository;

import jakarta.annotation.Nullable;
import java.util.List;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.dto.RecipeDataResponse;
import net.pengcook.recipe.dto.RecipeHomeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("""
            SELECT DISTINCT recipe.id
            FROM CategoryRecipe
            WHERE category.name = :category
            ORDER BY recipe.id DESC
            """)
    List<Long> findRecipeIdsByCategory(Pageable pageable, String category);

    @Query("""
            SELECT DISTINCT id
            FROM Recipe
            WHERE title LIKE CONCAT('%', :keyword, '%')
            OR description LIKE CONCAT('%', :keyword, '%')
            ORDER BY id DESC
            """)
    List<Long> findRecipeIdsByKeyword(Pageable pageable, String keyword);

    List<Recipe> findRecipeByAuthorIdOrderByIdDesc(Pageable pageable, long authorId);

    @Query("""
            SELECT DISTINCT r.id
            FROM Recipe r
            LEFT JOIN CategoryRecipe cr ON cr.recipe = r
            LEFT JOIN Category c ON cr.category = c
            WHERE (:category IS NULL OR c.name = :category)
            AND (:keyword IS NULL OR CONCAT(r.title, r.description) LIKE CONCAT('%', :keyword, '%'))
            AND (:userId IS NULL OR r.author.id = :userId)
            ORDER BY r.id DESC
            """)
    List<Long> findRecipeIdsByCategoryAndKeyword(
            Pageable pageable,
            @Param("category") @Nullable String category,
            @Param("keyword") @Nullable String keyword,
            @Param("userId") @Nullable Long userId
    );

    @Query("""
            SELECT new net.pengcook.recipe.dto.RecipeDataResponse(
                r.id,
                r.title,
                r.author.id,
                r.author.username,
                r.author.image,
                r.cookingTime,
                r.thumbnail,
                r.difficulty,
                r.likeCount,
                r.commentCount,
                r.description,
                r.createdAt,
                c.id,
                c.name,
                i.id,
                i.name,
                ir.requirement
            )
            FROM Recipe r
            JOIN FETCH CategoryRecipe cr ON cr.recipe = r
            JOIN FETCH Category c ON cr.category = c
            JOIN FETCH IngredientRecipe ir ON ir.recipe = r
            JOIN FETCH Ingredient i ON ir.ingredient = i
            WHERE r.id IN :recipeIds
            """)
    List<RecipeDataResponse> findRecipeData(List<Long> recipeIds);

    @Query("""
            SELECT new net.pengcook.recipe.dto.RecipeHomeResponse(
                r.id,
                r.title,
                r.author.id,
                r.author.username,
                r.author.image,
                r.thumbnail,
                r.likeCount,
                r.commentCount,
                r.createdAt
            )
            FROM Recipe r
            WHERE r.id IN :recipeIds
            """)
    List<RecipeHomeResponse> findRecipeDataV1(List<Long> recipeIds);

    @Query("""
            SELECT r.id
            FROM Recipe r
            WHERE r.author.id = :userId
            """)
    List<Long> findRecipeIdsByUserId(long userId);

    int countByAuthorId(long userId);
}
