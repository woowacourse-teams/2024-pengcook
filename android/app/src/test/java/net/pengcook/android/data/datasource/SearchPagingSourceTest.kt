package net.pengcook.android.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.model.User
import net.pengcook.android.presentation.search.SearchEventListener
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SearchPagingSourceTest {
    private lateinit var feedRepository: FeedRepository
    private lateinit var searchEventListener: SearchEventListener
    private lateinit var pagingSource: SearchPagingSource

    @Before
    fun setup() {
        searchEventListener = mockk(relaxed = true)
    }

    @Test
    fun `load returns Page on successful load`() =
        runTest {
            feedRepository = mockk()
            pagingSource = SearchPagingSource(feedRepository, searchEventListener)
            val recipes = recipes()

            coEvery { feedRepository.fetchRecipes(any(), any()) } returns Result.success(recipes)

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

    @Test
    fun `getRefreshKey returns correct key`() {
        feedRepository = mockk()
        pagingSource = SearchPagingSource(feedRepository, searchEventListener)
        val state =
            PagingState(
                pages =
                    listOf(
                        PagingSource.LoadResult.Page(
                            data = recipes(),
                            prevKey = null,
                            nextKey = 1,
                        ),
                    ),
                anchorPosition = 1,
                config = PagingConfig(pageSize = 2),
                leadingPlaceholderCount = 0,
            )

        val key = pagingSource.getRefreshKey(state)

        assertEquals(0, key)
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
                favoriteCount = 100,
                ingredients = listOf(),
                difficulty = 1,
                introduction = "Delicious fish cake",
                commentCount = 10,
            ),
            Recipe(
                recipeId = 2,
                title = "Chicken Soup",
                category = listOf("Soup"),
                cookingTime = "45 min",
                thumbnail = "",
                user = User(2, "User2", "profile2"),
                favoriteCount = 150,
                ingredients = listOf(),
                difficulty = 2,
                introduction = "Healthy chicken soup",
                commentCount = 20,
            ),
        )
}
