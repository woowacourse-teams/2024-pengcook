package net.pengcook.android.presentation.step

import android.os.SystemClock
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemStepRecipeBinding
import net.pengcook.android.presentation.core.model.RecipeStep

class RecipeStepViewHolder(
    private val binding: ItemStepRecipeBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(recipeStep: RecipeStep) {
        binding.recipeStep = recipeStep
        binding.timer.chronometer.format = "%MM:ss"
        binding.timer.chronometer.base = SystemClock.elapsedRealtime() + recipeStep.sequence * 1000
    }
}
