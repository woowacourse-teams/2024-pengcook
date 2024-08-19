package net.pengcook.android.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import net.pengcook.android.data.model.makingrecipe.entity.IngredientEntity

@Dao
interface IngredientDao {
    @Upsert
    suspend fun saveIngredients(ingredients: List<IngredientEntity>): List<Long>

    @Query("DELETE FROM ingredient")
    suspend fun deleteAllIngredients()

    @Query("SELECT * FROM ingredient")
    suspend fun fetchAllIngredients(): List<IngredientEntity>

    @Query("SELECT * FROM ingredient WHERE id = :ingredientId")
    suspend fun fetchIngredientById(ingredientId: Long): IngredientEntity

    @Query("SELECT * FROM ingredient WHERE recipe_description_id = :recipeId")
    suspend fun fetchIngredientsByRecipeId(recipeId: Long): List<IngredientEntity>

    @Query("DELETE FROM ingredient WHERE recipe_description_id = :recipeId")
    suspend fun deleteIngredientsByRecipeId(recipeId: Long)
}
