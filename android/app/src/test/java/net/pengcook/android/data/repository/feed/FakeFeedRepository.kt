package net.pengcook.android.data.repository.feed

import net.pengcook.android.presentation.core.model.ChangedRecipe
import net.pengcook.android.presentation.core.model.RecipeForItem
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.core.model.RecipeStep
import net.pengcook.android.presentation.core.model.User

class FakeFeedRepository(
    private val error: Boolean = false,
) : FeedRepository {
    private var pageNumber = 0

    override suspend fun fetchRecipes(
        pageNumber: Int,
        pageSize: Int,
        category: String?,
        keyword: String?,
        userId: Long?,
    ): Result<List<RecipeForList>> =
        runCatching {
            if (error) {
                throw RuntimeException()
            }
            List(pageSize) { index ->
                RecipeForList(
                    recipeId = pageNumber * pageSize + index.toLong(),
                    title = "recipe$index",
                    thumbnail = "thumbnail$index",
                    user = User(1, "user$index", "userThumbnail$index"),
                    likeCount = index.toLong(),
                    commentCount = 10,
                    mine = false,
                    createdAt = "",
                )
            }
        }

    override suspend fun fetchRecipeSteps(recipeId: Long): Result<List<RecipeStep>> =
        runCatching {
            List(10) {
                RecipeStep(it.toLong() + 1, 1L, "description${it + 1}", "image${it + 1}", it + 1, "10")
            }
        }

    override suspend fun deleteRecipe(recipeId: Long): Result<Unit> =
        runCatching {
            Unit
        }

    override suspend fun fetchRecipe(recipeId: Long): Result<RecipeForItem> =
        runCatching {
            RecipeForItem(
                recipeId = recipeId,
                title = "recipe$recipeId",
                category = listOf("category$recipeId"),
                cookingTime = "cookingTime$recipeId",
                thumbnail = "thumbnail$recipeId",
                user = User(1, "user$recipeId", "userThumbnail$recipeId"),
                likeCount = recipeId,
                ingredients = listOf(),
                difficulty = 1,
                introduction = "introduction$recipeId",
                commentCount = 10,
                mine = false,
                isLiked = false,
            )
        }

    override suspend fun updateRecipe(
        recipeId: Long,
        changedRecipe: ChangedRecipe,
    ): Result<Unit> {
        TODO("Not yet implemented")
    }
}
