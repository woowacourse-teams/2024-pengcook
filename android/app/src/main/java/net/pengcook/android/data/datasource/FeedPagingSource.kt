package net.pengcook.android.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.pengcook.android.presentation.core.model.Feed

class FeedPagingSource(
    private val initialPageNumber: Int = 0,
    private val fetchFeeds: suspend (pageNumber: Int, size: Int) -> List<Feed>,
) : PagingSource<Int, Feed>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> {
        val pageNumber = params.key ?: initialPageNumber
        return runCatching {
            val feeds = fetchFeeds(pageNumber, params.loadSize)
            LoadResult.Page(
                data = feeds,
                prevKey = if (pageNumber == initialPageNumber) null else pageNumber - 1,
                nextKey = pageNumber + 1,
            )
        }.onFailure { throwable ->
            LoadResult.Error<Int, Feed>(throwable)
        }.getOrThrow()
    }

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
