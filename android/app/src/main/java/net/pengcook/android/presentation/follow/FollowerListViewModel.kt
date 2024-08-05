package net.pengcook.android.presentation.follow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import net.pengcook.android.presentation.core.listener.UserManipulationButtonClickListener
import net.pengcook.android.presentation.core.model.User
import java.util.UUID

class FollowerListViewModel :
    ViewModel(),
    UserManipulationButtonClickListener,
    UserItemClickListener {
    val pagingDataFlow =
        Pager(PagingConfig(pageSize = 20)) {
            FollowPagingSource(
                fetchUsers =
                    suspend {
                        runCatching {
                            List(10) {
                                User(
                                    UUID.randomUUID().hashCode().toLong(),
                                    "user",
                                    "https://h5p.org/sites/default/files/h5p/content/1209180/images/file-6113d5f8845dc.jpeg",
                                )
                            }
                        }
                    },
            )
        }.liveData.cachedIn(viewModelScope)

    val keyword: MutableLiveData<String> = MutableLiveData("")

    override fun onUserItemSelect(userId: Long) {
    }

    override fun onUserManipulation(userId: Long) {
    }
}
