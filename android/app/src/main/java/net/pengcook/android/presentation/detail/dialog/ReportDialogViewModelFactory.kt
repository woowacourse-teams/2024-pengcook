package net.pengcook.android.presentation.detail.dialog

import dagger.assisted.AssistedFactory
import net.pengcook.android.presentation.core.model.RecipeForItem

@AssistedFactory
interface ReportDialogViewModelFactory {
    fun create(recipe: RecipeForItem): ReportDialogViewModel
}
