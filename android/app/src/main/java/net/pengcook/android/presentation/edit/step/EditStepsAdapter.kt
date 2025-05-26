package net.pengcook.android.presentation.edit.step

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import net.pengcook.android.databinding.ItemEditStepBinding
import net.pengcook.android.presentation.core.model.RecipeStepMaking
import net.pengcook.android.presentation.making.newstep.OnStepDataChangeListener

class EditStepsAdapter(
    private val onStepDataChangeListener: OnStepDataChangeListener,
) : ListAdapter<RecipeStepMaking, EditStepsViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EditStepsViewHolder {
        val binding =
            ItemEditStepBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return EditStepsViewHolder(binding, onStepDataChangeListener)
    }

    override fun onBindViewHolder(
        holder: EditStepsViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<RecipeStepMaking>() {
                override fun areItemsTheSame(
                    oldItem: RecipeStepMaking,
                    newItem: RecipeStepMaking,
                ): Boolean = oldItem.stepId == newItem.stepId

                override fun areContentsTheSame(
                    oldItem: RecipeStepMaking,
                    newItem: RecipeStepMaking,
                ): Boolean = oldItem == newItem
            }
    }
}
