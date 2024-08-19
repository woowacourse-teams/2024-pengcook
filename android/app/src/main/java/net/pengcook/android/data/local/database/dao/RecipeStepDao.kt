package net.pengcook.android.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.pengcook.android.data.model.step.RecipeStepEntity

@Dao
interface RecipeStepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreatedRecipeStep(recipeStep: RecipeStepEntity): Long

    @Query("SELECT * FROM recipe_step WHERE recipe_description_id = :recipeId")
    suspend fun fetchRecipeStepsByRecipeId(recipeId: Long): List<RecipeStepEntity>?

    @Query("SELECT * FROM recipe_step WHERE recipe_description_id = :recipeId AND step_number = :stepNumber")
    suspend fun fetchRecipeStepByStepNumber(
        recipeId: Long,
        stepNumber: Int,
    ): RecipeStepEntity?

    @Query("DELETE FROM recipe_step WHERE recipe_description_id = :recipeId")
    suspend fun deleteRecipeStepsByRecipeId(recipeId: Long)
}
