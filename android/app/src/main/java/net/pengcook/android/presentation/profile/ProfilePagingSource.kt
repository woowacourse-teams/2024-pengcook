package net.pengcook.android.presentation.profile

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.pengcook.android.presentation.core.model.Recipe

class ProfilePagingSource(
    private val initialPageNumber: Int = 0,
    private val fetchProfile: suspend () -> Result<Profile>,
    private val fetchFeeds: suspend (pageNumber: Int, size: Int) -> Result<List<Recipe>>,
) : PagingSource<Int, ProfileViewItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult.Page<Int, ProfileViewItem> {
        val pageNumber = params.key ?: initialPageNumber
        return runCatching {
            if (pageNumber == initialPageNumber) {
                val currentList = mutableListOf<ProfileViewItem>()
                val profile = fetchProfile().getOrThrow()
                currentList.apply {
                    add(ProfileViewItem.ProfileDescription(profile))
                    add(ProfileViewItem.ProfileButtons())
                }
                val feeds = fetchFeeds(pageNumber, params.loadSize - 2)
                val pageData =
                    feeds.getOrNull()?.map { recipe ->
                        ProfileViewItem.ProfileFeeds(recipe)
                    } ?: emptyList()
                currentList.addAll(pageData)
                val nextKey = if (pageData.size < params.loadSize - 2) null else pageNumber + 1
                LoadResult.Page(
                    data = currentList,
                    prevKey = null,
                    nextKey = nextKey,
                )
            } else {
                val feeds = fetchFeeds(pageNumber, params.loadSize)
                val pageData =
                    feeds.getOrNull()?.map { recipe ->
                        ProfileViewItem.ProfileFeeds(recipe)
                    } ?: emptyList()
                val nextKey = if (pageData.size < params.loadSize) null else pageNumber + 1
                LoadResult.Page(
                    data = pageData,
                    prevKey = pageNumber - 1,
                    nextKey = nextKey,
                ) as LoadResult.Page<Int, ProfileViewItem>
            }
        }.onFailure { throwable ->
            LoadResult.Error<Int, Recipe>(throwable)
        }.getOrThrow()
    }

    override fun getRefreshKey(state: PagingState<Int, ProfileViewItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
