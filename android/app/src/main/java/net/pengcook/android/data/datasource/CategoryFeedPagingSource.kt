package net.pengcook.android.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.pengcook.android.presentation.core.model.Recipe

class CategoryFeedPagingSource(
    private val initialPageNumber: Int = 0,
    private val category: String,
    private val fetchFeeds: suspend (pageNumber: Int, size: Int, category: String) -> Result<List<Recipe>>,
) : PagingSource<Int, Recipe>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {
        val pageNumber = params.key ?: initialPageNumber
        return runCatching {
            val feeds = fetchFeeds(pageNumber, params.loadSize, category)
            val pageData = feeds.getOrNull() ?: emptyList()
            val nextKey = if (pageData.size < params.loadSize) null else pageNumber + 1
            LoadResult.Page(
                data = pageData,
                prevKey = if (pageNumber == initialPageNumber) null else pageNumber - 1,
                nextKey = nextKey,
            )
        }.onFailure { throwable ->
            LoadResult.Error<Int, Recipe>(throwable)
        }.getOrThrow()
    }

    override fun getRefreshKey(state: PagingState<Int, Recipe>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
