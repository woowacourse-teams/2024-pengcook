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

        val timerClickListener = View.OnClickListener {
            showTimerSettingDialog()
        }
        binding.clickListener = timerClickListener

        val contentClickListener = View.OnClickListener {
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
        dialog.show(fragmentManager, DIALOG_TAG)
    }

    override fun onTimeSet(
        minutes: Int,
        seconds: Int,
    ) {
        val totalMillis = (minutes * SECONDS_IN_MINUTE + seconds) * MILLIS_IN_SECOND
        cookingTimeInMillis = totalMillis

        binding.timer.tvTimer.text = formatTime(cookingTimeInMillis)
        isTimerRunning = false
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun startTimer(durationInMillis: Long) {
        countDownTimer = object : CountDownTimer(durationInMillis, TIMER_INTERVAL_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.tvTimer.text = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                binding.timer.tvTimer.text = INITIAL_TIME_TEXT
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
        val totalSeconds = millis / MILLIS_IN_SECOND
        val minutes = totalSeconds / SECONDS_IN_MINUTE
        val seconds = totalSeconds % SECONDS_IN_MINUTE
        return String.format(TIME_FORMAT, minutes, seconds)
    }

    private fun convertToMillis(time: String): Long {
        val timeParts = time.split(":")
        val hours = timeParts.getOrNull(0)?.toLongOrNull() ?: 0L
        val minutes = timeParts.getOrNull(1)?.toLongOrNull() ?: 0L
        val seconds = timeParts.getOrNull(2)?.toLongOrNull() ?: 0L
        return (hours * SECONDS_IN_HOUR + minutes * SECONDS_IN_MINUTE + seconds) * MILLIS_IN_SECOND
    }

    companion object {
        private const val TIMER_INTERVAL_MILLIS = 1000L
        private const val MILLIS_IN_SECOND = 1000L
        private const val SECONDS_IN_MINUTE = 60L
        private const val SECONDS_IN_HOUR = 3600L
        private const val INITIAL_TIME_TEXT = "00:00"
        private const val TIME_FORMAT = "%02d:%02d"
        private const val DIALOG_TAG = "TimerSettingDialog"
    }
}
