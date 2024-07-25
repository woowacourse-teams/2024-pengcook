package net.pengcook.android.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.pengcook.android.data.model.SearchData

class SearchPagingSource : PagingSource<Int, SearchData>() {
    override fun getRefreshKey(state: PagingState<Int, SearchData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchData> {
        return try {
            val page = params.key ?: 0
            // Start paging with the STARTING_KEY if this is the first load
            val start = params.key ?: STARTING_KEY
            // Load as many items as hinted by params.loadSize
            val range = start.until(start + params.loadSize)

            val data =
                range.map { number ->
                    SearchData(
                        // Generate consecutive increasing numbers as the article id
                        id = number.toLong(),
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTa6wAyeNbJl6jU-OAz4pTCeczAuPaXSWLTcw&s",
                    )
                }

            val prevKey = if (page == 0) null else page - 1
            val nextKey = if (data.isEmpty()) null else page + 1

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    companion object {
        private const val STARTING_KEY = 0
        private const val LOAD_DELAY_MILLIS = 3_000L
    }
}
