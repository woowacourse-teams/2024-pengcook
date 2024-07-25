package net.pengcook.android.presentation.making.step

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.pengcook.android.presentation.making.step.listener.StepEventListener

class StepMakingViewModel : ViewModel(), StepEventListener {
    private val _stepNumber = MutableLiveData<String>()
    val stepNumber: LiveData<String> get() = _stepNumber

    val introductionContent = MutableLiveData<String>()

    override fun onNavigateToNextStep() {
        TODO("Not yet implemented")
    }
}
