package net.pengcook.android.data.repository.feed

import net.pengcook.android.presentation.core.model.Recipe
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
    ): Result<List<Recipe>> =
        runCatching {
            if (error) {
                throw RuntimeException()
            }
            List(pageSize) { index ->
                Recipe(
                    recipeId = pageNumber * pageSize + index.toLong(),
                    title = "recipe$index",
                    category = listOf("Korean"),
                    cookingTime = "10mins",
                    thumbnail = "thumbnail$index",
                    user = User(1, "user$index", "userThumbnail$index"),
                    likeCount = index.toLong(),
                    ingredients = listOf(),
                    difficulty = 5,
                    introduction = "introduction$index",
                    commentCount = 10,
                    mine = false,
                )
            }
        }

    override suspend fun fetchRecipeSteps(recipeId: Long): Result<List<RecipeStep>> =
        runCatching {
            List(10) {
                RecipeStep(it.toLong() + 1, 1L, "description${it + 1}", "image${it + 1}", it + 1)
            }
        }

    override suspend fun deleteRecipe(recipeId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }
}