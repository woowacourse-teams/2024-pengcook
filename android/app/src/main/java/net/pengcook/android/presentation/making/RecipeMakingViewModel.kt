package net.pengcook.android.presentation.making

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.pengcook.android.presentation.making.listener.RecipeMakingEventListener

class RecipeMakingViewModel : ViewModel(), RecipeMakingEventListener {
    val titleContent = MutableLiveData<String>()

    val categorySelectedValue = MutableLiveData<String>()

    val ingredientContent = MutableLiveData<String>()

    val difficultySelectedValue = MutableLiveData<String>()

    val introductionContent = MutableLiveData<String>()

    override fun onNavigateToStep() {
        // TODO: Implement navigation logic
    }
}
