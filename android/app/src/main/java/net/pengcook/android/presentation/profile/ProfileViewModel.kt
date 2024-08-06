package net.pengcook.android.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.model.User

class ProfileViewModel : ViewModel(), DoubleButtonClickListener, ProfileFeedClickListener {
    val items: LiveData<PagingData<ProfileViewItem>> =
        Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                ProfilePagingSource(
                    fetchProfile =
                        suspend {
                            runCatching {
                                Profile(
                                    "test1",
                                    "nickname1",
                                    "https://h5p.org/sites/default/files/h5p/content/1209180/images/file-6113d5f8845dc.jpeg",
                                    1L,
                                    2L,
                                    "",
                                )
                            }
                        },
                    fetchFeeds = this::getFeed,
                )
            },
        )
            .liveData
            .cachedIn(viewModelScope)

    private suspend fun getFeed(
        pageNumber: Int,
        size: Int,
    ): Result<List<Recipe>> {
        return runCatching {
            List(size) {
                Recipe(
                    it.toLong(),
                    "recipe",
                    emptyList(),
                    "",
                    "https://h5p.org/sites/default/files/h5p/content/1209180/images/file-6113d5f8845dc.jpeg",
                    User(1L, "user1", "https://h5p.org/sites/default/files/h5p/content/1209180/images/file-6113d5f8845dc.jpeg"),
                    1L,
                    emptyList(),
                    1,
                    "",
                    1,
                )
            }
        }
    }

    override fun onLeftButtonClick() {
    }

    override fun onRightButtonClick() {
    }

    override fun onClick(recipeId: Long) {
    }
}
