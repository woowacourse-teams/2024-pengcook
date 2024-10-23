package net.pengcook.android.presentation.making.newstep

import android.text.InputFilter.LengthFilter
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemStepMakingBinding
import net.pengcook.android.presentation.core.model.RecipeStepMaking
import net.pengcook.android.presentation.core.util.MinMaxInputFilter

class NewStepMakingViewHolder(
    private val binding: ItemStepMakingBinding,
    private val onStepDataChangeListener: OnStepDataChangeListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        val etMinute = binding.etTimeAmount.etTimeAmountPicker.etMinute
        val etSecond = binding.etTimeAmount.etTimeAmountPicker.etSecond
        arrayOf(MinMaxInputFilter(0, 59), LengthFilter(2)).also { filters ->
            etMinute.filters = filters
            etSecond.filters = filters
        }
    }

    fun bind(recipeStepMaking: RecipeStepMaking) {
        binding.recipeStepMaking = recipeStepMaking

        binding.etTimeAmount.etTimeAmountPicker.etSecond.addTextChangedListener {
            onStepDataChangeListener.onSecondChanged(recipeStepMaking.sequence - 1, it.toString())
        }

        binding.etTimeAmount.etTimeAmountPicker.etMinute.addTextChangedListener {
            onStepDataChangeListener.onMinuteChanged(recipeStepMaking.sequence - 1, it.toString())
        }

        binding.etDescStepRecipe.etContent.etFormTextContent.etDefault.addTextChangedListener {
            onStepDataChangeListener.onDescriptionChanged(
                recipeStepMaking.sequence - 1,
                it.toString(),
            )
        }
    }
}
