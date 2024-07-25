package net.pengcook.android.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import net.pengcook.android.data.datasource.SearchPagingSource
import net.pengcook.android.data.model.SearchData

class SearchViewModel : ViewModel() {
    val searchKeyword: MutableLiveData<String> = MutableLiveData()

    val items: LiveData<PagingData<SearchData>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { SearchPagingSource() },
        )
            .liveData
            .cachedIn(viewModelScope)

    companion object {
        private const val PAGE_SIZE = 10
    }
}

