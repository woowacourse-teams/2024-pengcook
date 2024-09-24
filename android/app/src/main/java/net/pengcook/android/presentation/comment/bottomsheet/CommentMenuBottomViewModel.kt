package net.pengcook.android.presentation.comment.bottomsheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.usercontrol.UserControlRepository
import net.pengcook.android.presentation.core.model.ReportReason
import javax.inject.Inject

@HiltViewModel
class CommentMenuBottomViewModel @Inject constructor(
    private val userControlRepository: UserControlRepository,
) : ViewModel(),
    ReportEventHandler {
    private var _reportReasons = MutableLiveData<List<ReportReason>>()
    val reportReasons: LiveData<List<ReportReason>>
        get() = _reportReasons

    private var _reportState = MutableLiveData<Boolean>(false)
    val reportState: LiveData<Boolean>
        get() = _reportState

    private var _selectedReportReason = MutableLiveData<ReportReason>()
    val selectedReportReason: LiveData<ReportReason>
        get() = _selectedReportReason

    init {
        loadReportReasons()
    }

    private fun loadReportReasons() {
        viewModelScope.launch {
            userControlRepository.fetchReportReasons().onSuccess {
                _reportReasons.value = it
            }
        }
    }

    fun onReport() {
        _reportState.value = true
    }

    override fun onCompleteReport(reportReason: ReportReason) {
        _selectedReportReason.value = reportReason
    }
}
