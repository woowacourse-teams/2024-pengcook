package net.pengcook.android.presentation.detail.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.pengcook.android.data.repository.usercontrol.UserControlRepository
import net.pengcook.android.presentation.core.model.Recipe

class ReportDialogViewModelFactory(
    private val userControlRepository: UserControlRepository,
    private val recipe: Recipe,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportDialogViewModel::class.java)) {
            return ReportDialogViewModel(
                userControlRepository = userControlRepository,
                recipe = recipe,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
