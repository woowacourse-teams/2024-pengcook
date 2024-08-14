package net.pengcook.android.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import net.pengcook.android.data.model.making.Recipe
import net.pengcook.android.data.model.making.RecipeStep
import net.pengcook.android.data.model.making.RecipeWithSteps

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeStep(recipeStep: RecipeStep): Long

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun fetchRecipeWithSteps(recipeId: Long): List<RecipeWithSteps>

    // fetch one step data with sequence parameter
    @Query("SELECT * FROM recipe_steps WHERE recipeId = :recipeId AND stepNumber = :stepNumber")
    suspend fun fetchRecipeStepWithStepNumber(
        recipeId: Long,
        stepNumber: Int,
    ): RecipeStep

    // delete logic
    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipe(recipeId: Long)
}
