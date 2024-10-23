package net.pengcook.android.presentation.making.newstep

interface OnStepDataChangeListener {
    fun onDescriptionChanged(
        sequence: Int,
        description: String,
    )

    fun onMinuteChanged(
        sequence: Int,
        minute: String,
    )

    fun onSecondChanged(
        sequence: Int,
        second: String,
    )
}
