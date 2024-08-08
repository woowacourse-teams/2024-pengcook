package net.pengcook.android.presentation.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.pengcook.android.data.repository.feed.FakeFeedRepository
import net.pengcook.android.data.repository.feed.FeedRepository
import net.pengcook.android.presentation.util.getOrAwaitValue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchViewModelTest {
    private lateinit var feedRepository: FeedRepository
    private lateinit var viewModel: SearchViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun navigationToDetail_success() =
        runTest {
            // given
            feedRepository = FakeFeedRepository()
            viewModel = SearchViewModel(feedRepository)

            // when
            val recipe =
                feedRepository.fetchRecipes(
                    pageNumber = 0,
                    pageSize = 20,
                    category = null,
                    keyword = null,
                    userId = null,
                ).getOrThrow().first()
            viewModel.onNavigateToDetail(recipe)

            // then
            val actual = viewModel.uiEvent.getOrAwaitValue().getContentIfNotHandled()
            assertEquals(SearchUiEvent.RecipeSelected(recipe), actual)
        }

    @Test
    fun fetchData_failure_notifiesError() =
        runTest {
            // given
            feedRepository = FakeFeedRepository()
            viewModel = SearchViewModel(feedRepository)

            // when
            viewModel.onSearchError()

            // then
            val actual = viewModel.uiEvent.getOrAwaitValue().getContentIfNotHandled()
            assertEquals(SearchUiEvent.SearchFailure, actual)
        }
}
