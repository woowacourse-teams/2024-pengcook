package net.pengcook.android.presentation.step

import android.os.CountDownTimer
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemStepRecipeBinding
import net.pengcook.android.presentation.core.model.RecipeStep

class RecipeStepViewHolder(
    private val binding: ItemStepRecipeBinding,
) : RecyclerView.ViewHolder(binding.root) {

    private var isTimerRunning = false
    private var countDownTimer: CountDownTimer? = null

    fun bind(recipeStep: RecipeStep, moveToNextStep: () -> Unit) {
        binding.recipeStep = recipeStep

        val cookingTimeInSeconds = convertToSeconds(recipeStep.cookingTime)

        binding.timer.tvTimer.text = formatTime(cookingTimeInSeconds * 1000)

        val clickListener = View.OnClickListener {
            when {
                cookingTimeInSeconds <= 1 -> {
                    moveToNextStep()
                }

                !isTimerRunning -> {
                    startTimer(cookingTimeInSeconds * 1000, moveToNextStep)
                    isTimerRunning = true
                }

                else -> {
                    stopTimer()
                    moveToNextStep()
                }
            }
        }
        binding.clickListener = clickListener

        binding.executePendingBindings()
    }

    private fun startTimer(durationInMillis: Long, moveToNextStep: () -> Unit) {
        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.tvTimer.text = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                binding.timer.tvTimer.text = "00:00"
                isTimerRunning = false
                moveToNextStep()
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

    private fun convertToSeconds(time: String): Long {
        val timeParts = time.split(":")
        val hours = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
        val minutes = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
        val seconds = timeParts.getOrNull(2)?.toIntOrNull() ?: 0
        return (hours * 3600 + minutes * 60 + seconds).toLong()
    }
}
