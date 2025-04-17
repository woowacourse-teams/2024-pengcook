package net.pengcook.android.presentation.edit.step

import android.text.InputFilter
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemEditStepBinding
import net.pengcook.android.presentation.core.model.RecipeStepMaking
import net.pengcook.android.presentation.core.util.MinMaxInputFilter
import net.pengcook.android.presentation.making.newstep.OnStepDataChangeListener

class EditStepsViewHolder(
    private val binding: ItemEditStepBinding,
    private val onStepDataChangeListener: OnStepDataChangeListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        val etMinute = binding.etTimeAmount.etTimeAmountPicker.etMinute
        val etSecond = binding.etTimeAmount.etTimeAmountPicker.etSecond
        arrayOf(MinMaxInputFilter(0, 59), InputFilter.LengthFilter(2)).also { filters ->
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
