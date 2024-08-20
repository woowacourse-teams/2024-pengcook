package net.pengcook.android.presentation.detail.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.usercontrol.UserControlRepository
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.model.ReportReason

class ReportDialogViewModel(
    private val userControlRepository: UserControlRepository,
    private val recipe: Recipe,
) : ViewModel(),
    ReportEventHandler {
    private var _reportReasons = MutableLiveData<List<ReportReason>>()
    val reportReasons: LiveData<List<ReportReason>>
        get() = _reportReasons

    private var _reportState = MutableLiveData<Boolean>(false)
    val reportState: LiveData<Boolean>
        get() = _reportState

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

    override fun onCompleteReport(reportReason: ReportReason) {
        viewModelScope.launch {
            userControlRepository
                .reportUser(
                    reporteeId = recipe.user.id,
                    reason = reportReason.reason,
                    type = "RECIPE",
                    targetId = recipe.recipeId,
                    details = null,
                ).onSuccess {
                    _reportState.value = true
                }
        }
    }
}
