package net.pengcook.android.presentation.making.listener

import net.pengcook.android.presentation.signup.BottomButtonClickListener

interface RecipeMakingEventListener : BottomButtonClickListener {
    fun onAddImage()

    fun onAddStepImages()

    override fun onConfirm()
}
