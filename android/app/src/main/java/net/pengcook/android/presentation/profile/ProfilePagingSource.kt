package net.pengcook.android.presentation.profile

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.pengcook.android.data.repository.profile.ProfileRepository
import net.pengcook.android.domain.model.profile.UserProfile
import net.pengcook.android.presentation.core.model.Recipe

class ProfilePagingSource(
    private val initialPageNumber: Int = 0,
    private val profileFeedType: ProfileFeedType,
    private val profileRepository: ProfileRepository,
) : PagingSource<Int, ProfileViewItem>() {
    private var userId: Long? = null

    // TODO 로직 고치기
    override suspend fun load(params: LoadParams<Int>): LoadResult.Page<Int, ProfileViewItem> {
        val pageNumber = params.key ?: initialPageNumber
        return runCatching {
            if (pageNumber == initialPageNumber) {
                var pageData: List<ProfileViewItem> = emptyList()
                fetchProfile()
                    .onSuccess { userProfile ->
                        userId = userProfile.id
                        pageData += ProfileViewItem.ProfileDescription(userProfile)
                        pageData += ProfileViewItem.ProfileButtons()
                    }.getOrThrow()
                val feeds = profileFeeds(pageNumber, params.loadSize - 2)
                pageData += feeds
                val nextKey = if (pageData.size < params.loadSize - 2) null else pageNumber + 1
                LoadResult.Page(
                    data = pageData,
                    prevKey = null,
                    nextKey = nextKey,
                )
            } else {
                var pageData: List<ProfileViewItem> = emptyList()
                val feeds = profileFeeds(pageNumber, params.loadSize)
                pageData += feeds
                val nextKey = if (pageData.size < params.loadSize) null else pageNumber + 1
                LoadResult.Page(
                    data = pageData,
                    prevKey = pageNumber - 1,
                    nextKey = nextKey,
                )
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

    private suspend fun profileFeeds(
        pageNumber: Int,
        pageSize: Int,
    ): List<ProfileViewItem.ProfileFeeds> {
        return userId?.let { id ->
            fetchFeeds(id, pageNumber, pageSize).getOrNull()?.map { recipe ->
                ProfileViewItem.ProfileFeeds(recipe)
            } ?: emptyList()
        } ?: emptyList()
    }

    private suspend fun fetchProfile(): Result<UserProfile> {
        return when (profileFeedType) {
            is ProfileFeedType.MyFeed -> profileRepository.fetchMyUserInformation()
            is ProfileFeedType.OthersFeed -> profileRepository.fetchUserInformation(profileFeedType.userId)
        }
    }

    private suspend fun fetchFeeds(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): Result<List<Recipe>> {
        return profileRepository.fetchUserFeeds(userId, pageNumber, pageSize)
    }
}
