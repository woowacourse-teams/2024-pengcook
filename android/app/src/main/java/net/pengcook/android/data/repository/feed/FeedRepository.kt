package net.pengcook.android.data.repository.feed

import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.model.RecipeStep

interface FeedRepository {
    suspend fun fetchRecipes(
        pageNumber: Int,
        pageSize: Int,
        category: String?,
        keyword: String?,
        userId: Long?,
    ): Result<List<Recipe>>

    suspend fun fetchRecipeSteps(recipeId: Long): Result<List<RecipeStep>>

    suspend fun deleteRecipe(recipeId: Long): Result<Unit>
}
