package net.pengcook.android.data.datasource

import androidx.paging.PagingSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.model.User
import org.junit.Assert.assertEquals
import org.junit.Test

class FeedPagingSourceTest {
    private lateinit var feedRepository: FeedRepository
    private lateinit var pagingSource: FeedPagingSource

    @Test
    fun `load returns Page on successful load`() =
        runTest {
            feedRepository = mockk()
            pagingSource = FeedPagingSource(feedRepository)
            val recipes = recipes()

            coEvery {
                feedRepository.fetchRecipes(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns Result.success(recipes)

            val params =
                PagingSource.LoadParams.Refresh<Int>(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false,
                )

            val result = pagingSource.load(params)
            val page = result as PagingSource.LoadResult.Page

            assertEquals(recipes, page.data)
            assertEquals(null, page.prevKey)
            assertEquals(1, page.nextKey)
        }

    private fun recipes() =
        listOf(
            Recipe(
                recipeId = 1,
                title = "Fish Cake",
                category = listOf("Seafood"),
                cookingTime = "30 min",
                thumbnail = "",
                user = User(1, "User1", "profile1"),
                likeCount = 100,
                ingredients = listOf(),
                difficulty = 1,
                introduction = "Delicious fish cake",
                commentCount = 10,
                mine = false,
            ),
            Recipe(
                recipeId = 2,
                title = "Chicken Soup",
                category = listOf("Soup"),
                cookingTime = "45 min",
                thumbnail = "",
                user = User(2, "User2", "profile2"),
                likeCount = 150,
                ingredients = listOf(),
                difficulty = 2,
                introduction = "Healthy chicken soup",
                commentCount = 20,
                mine = false,
            ),
        )
}
