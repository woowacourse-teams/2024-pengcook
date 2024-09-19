package net.pengcook.android.presentation.step

import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemStepRecipeBinding
import net.pengcook.android.presentation.core.model.RecipeStep

class RecipeStepViewHolder(
    private val binding: ItemStepRecipeBinding,
    private val fragmentManager: FragmentManager,
) : RecyclerView.ViewHolder(binding.root), TimerSettingDialogFragment.TimerSettingListener {
    private var isTimerRunning = false
    private var countDownTimer: CountDownTimer? = null

    private var moveToNextStep: (() -> Unit)? = null

    private var cookingTimeInMillis: Long = 0L

    fun bind(
        recipeStep: RecipeStep,
        moveToNextStep: () -> Unit,
    ) {
        this.moveToNextStep = moveToNextStep
        binding.recipeStep = recipeStep

        cookingTimeInMillis = convertToMillis(recipeStep.cookingTime)
        binding.timer.tvTimer.text = formatTime(cookingTimeInMillis)

        val timerClickListener =
            View.OnClickListener {
                showTimerSettingDialog()
            }
        binding.clickListener = timerClickListener

        val contentClickListener =
            View.OnClickListener {
                if (!isTimerRunning) {
                    startTimer(cookingTimeInMillis)
                    isTimerRunning = true
                } else {
                    stopTimer()
                    moveToNextStep()
                }
            }
        binding.contentClickListener = contentClickListener

        binding.executePendingBindings()
    }

    private fun showTimerSettingDialog() {
        val dialog = TimerSettingDialogFragment()
        dialog.listener = this
        dialog.show(fragmentManager, "TimerSettingDialog")
    }

    override fun onTimeSet(
        minutes: Int,
        seconds: Int,
    ) {
        val totalMillis = (minutes * 60 + seconds) * 1000L
        cookingTimeInMillis = totalMillis

        binding.timer.tvTimer.text = formatTime(cookingTimeInMillis)
        isTimerRunning = false
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun startTimer(durationInMillis: Long) {
        countDownTimer =
            object : CountDownTimer(durationInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.timer.tvTimer.text = formatTime(millisUntilFinished)
                }

                override fun onFinish() {
                    binding.timer.tvTimer.text = "00:00"
                    isTimerRunning = false
                    moveToNextStep?.invoke()
                }
            }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun convertToMillis(time: String): Long {
        val timeParts = time.split(":")
        val hours = timeParts.getOrNull(0)?.toLongOrNull() ?: 0L
        val minutes = timeParts.getOrNull(1)?.toLongOrNull() ?: 0L
        val seconds = timeParts.getOrNull(2)?.toLongOrNull() ?: 0L
        return (hours * 3600 + minutes * 60 + seconds) * 1000L
    }
}
