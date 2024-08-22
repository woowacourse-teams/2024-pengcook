package net.pengcook.android.presentation.step

import android.os.SystemClock
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemStepRecipeBinding
import net.pengcook.android.presentation.core.model.RecipeStep

class RecipeStepViewHolder(
    private val binding: ItemStepRecipeBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(recipeStep: RecipeStep, clickListner: View.OnClickListener) {
        binding.recipeStep = recipeStep
        binding.clickListener = clickListner
        // cookingTime을 초로 변환
        val cookingTimeInSeconds = convertToSeconds(recipeStep.cookingTime)

        // 타이머 설정
        binding.timer.chronometer.format = "%M:%S"
        binding.timer.chronometer.base = SystemClock.elapsedRealtime() + cookingTimeInSeconds * 1000

        binding.timer.setClickListener {
            startTimer()
        }

        binding.executePendingBindings()
    }

    private fun startTimer() {
        binding.timer.chronometer.start()
    }

    private fun convertToSeconds(time: String): Long {
        val timeParts = time.split(":")
        val hours = timeParts[0].toIntOrNull() ?: 0
        val minutes = timeParts[1].toIntOrNull() ?: 0
        val seconds = timeParts[2].toIntOrNull() ?: 0
        return (hours * 3600 + minutes * 60 + seconds).toLong()
    }
}
