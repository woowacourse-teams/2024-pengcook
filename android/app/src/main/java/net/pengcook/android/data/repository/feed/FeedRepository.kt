package net.pengcook.android.data.repository.feed

import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.detail.RecipeStep

interface FeedRepository {
    suspend fun fetchRecipes(
        pageNumber: Int,
        pageSize: Int,
    ): Result<List<Recipe>>

    suspend fun fetchRecipeSteps(recipeId: Long): Result<List<RecipeStep>>

    suspend fun fetchRecipesByCategory(
        pageNumber: Int,
        pageSize: Int,
        category: String,
    ): Result<List<Recipe>>
}
