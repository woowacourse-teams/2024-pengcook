package net.pengcook.android.data.datasource

import androidx.paging.PagingSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.presentation.core.model.RecipeForList
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
            RecipeForList(
                recipeId = 1,
                title = "Fish Cake",
                thumbnail = "",
                user = User(1, "User1", "profile1"),
                likeCount = 100,
                commentCount = 10,
                mine = false,
                createdAt = "",
            ),
            RecipeForList(
                recipeId = 2,
                title = "Chicken Soup",
                thumbnail = "",
                user = User(2, "User2", "profile2"),
                likeCount = 150,
                commentCount = 20,
                mine = false,
                createdAt = "",
            ),
        )
}
