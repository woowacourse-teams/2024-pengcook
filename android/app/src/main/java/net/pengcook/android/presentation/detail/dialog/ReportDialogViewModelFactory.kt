package net.pengcook.android.presentation.detail.dialog

import dagger.assisted.AssistedFactory
import net.pengcook.android.presentation.core.model.Recipe


@AssistedFactory
interface ReportDialogViewModelFactory {
    fun create(recipe: Recipe): ReportDialogViewModel
}
