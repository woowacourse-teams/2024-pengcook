package net.pengcook.android.presentation.detail

import androidx.lifecycle.ViewModel
import net.pengcook.android.domain.model.Recipe

class DetailRecipeViewModel(
    private val recipe: Recipe,
) : ViewModel()