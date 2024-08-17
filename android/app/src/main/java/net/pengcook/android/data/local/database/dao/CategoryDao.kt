package net.pengcook.android.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import net.pengcook.android.data.model.makingrecipe.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Upsert
    suspend fun saveCategories(category: List<CategoryEntity>)

    @Upsert
    suspend fun saveSingleCategory(category: CategoryEntity)

    @Query("SELECT * FROM category WHERE recipe_description_id = :recipeId")
    suspend fun fetchCategoriesByRecipeId(recipeId: Long): List<CategoryEntity>

    @Query("DELETE FROM category WHERE recipe_description_id = :recipeId")
    suspend fun deleteCategoriesByRecipeId(recipeId: Long)

    @Query("DELETE FROM category")
    suspend fun deleteAllCategories()
}
