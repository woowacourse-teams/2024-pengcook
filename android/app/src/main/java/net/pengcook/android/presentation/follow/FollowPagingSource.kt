package net.pengcook.android.presentation.follow

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.pengcook.android.presentation.core.model.User

class FollowPagingSource(
    private val initialPageNumber: Int = 0,
    private val fetchUsers: suspend () -> Result<List<User>>,
) : PagingSource<Int, User>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult.Page<Int, User> {
        val pageNumber = params.key ?: initialPageNumber
        return runCatching {
            val users = fetchUsers()
            val pageData = users.getOrNull() ?: emptyList()
            println(pageData.size)
            println(params.loadSize)
            val nextKey = if (pageData.size < params.loadSize) null else pageNumber + 1
            LoadResult.Page(
                data = pageData,
                prevKey = pageNumber - 1,
                nextKey = nextKey,
            )
        }.onFailure { throwable ->
            LoadResult.Error<Int, User>(throwable)
        }.getOrThrow()
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
