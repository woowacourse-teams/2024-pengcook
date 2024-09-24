package net.pengcook.android.presentation.detail.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import net.pengcook.android.data.repository.usercontrol.UserControlRepository
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.core.model.ReportReason

class ReportDialogViewModel @AssistedInject constructor(
    private val userControlRepository: UserControlRepository,
    @Assisted private val recipe: Recipe,
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

    companion object {
        fun provideFactory(
            assistedFactory: ReportDialogViewModelFactory,
            recipe: Recipe,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(recipe) as T
            }
        }
    }
}
