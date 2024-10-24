package net.pengcook.android.data.repository.feed

import net.pengcook.android.presentation.core.model.RecipeForItem
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.core.model.RecipeStep

interface FeedRepository {
    suspend fun fetchRecipes(
        pageNumber: Int,
        pageSize: Int,
        category: String?,
        keyword: String?,
        userId: Long?,
    ): Result<List<RecipeForList>>

    suspend fun fetchRecipeSteps(recipeId: Long): Result<List<RecipeStep>>

    suspend fun deleteRecipe(recipeId: Long): Result<Unit>

    suspend fun fetchRecipe(recipeId: Long): Result<RecipeForItem>
}
