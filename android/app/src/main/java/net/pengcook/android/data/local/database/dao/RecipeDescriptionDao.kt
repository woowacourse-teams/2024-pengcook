package net.pengcook.android.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import net.pengcook.android.data.model.makingrecipe.entity.CreatedRecipe
import net.pengcook.android.data.model.makingrecipe.entity.CreatedRecipeDescription
import net.pengcook.android.data.model.makingrecipe.entity.RecipeDescriptionEntity

@Dao
interface RecipeDescriptionDao {
    @Upsert
    suspend fun insertCreatedRecipeDescription(recipeDescription: RecipeDescriptionEntity): Long

    @Transaction
    @Query("SELECT * FROM recipe_description LIMIT 1")
    suspend fun fetchRecentlyCreatedRecipe(): CreatedRecipe?

    @Transaction
    @Query("SELECT * FROM recipe_description")
    suspend fun fetchAllCreatedRecipes(): List<CreatedRecipe>?

    @Transaction
    @Query("SELECT * FROM recipe_description WHERE id = :recipeId")
    suspend fun fetchCreatedRecipeById(recipeId: Long): CreatedRecipe?

    @Transaction
    @Query("SELECT * FROM recipe_description WHERE id = :recipeId")
    suspend fun fetchRecipeDescriptionById(recipeId: Long): CreatedRecipeDescription?

    @Transaction
    @Query("SELECT * FROM recipe_description LIMIT 1")
    suspend fun fetchLatestRecipeDescription(): CreatedRecipeDescription?

    @Query("DELETE FROM recipe_description WHERE id = :recipeId")
    suspend fun deleteCreatedRecipeById(recipeId: Long)

    @Query("DELETE FROM recipe_description")
    suspend fun deleteAllCreatedRecipes()
}
