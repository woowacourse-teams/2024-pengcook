package net.pengcook.android.presentation.making.newstep

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import net.pengcook.android.databinding.ItemStepMakingBinding
import net.pengcook.android.presentation.core.model.RecipeStepMaking

class NewStepMakingAdapter(
    private val onStepDataChangeListener: OnStepDataChangeListener,
) : ListAdapter<RecipeStepMaking, NewStepMakingViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NewStepMakingViewHolder {
        val binding =
            ItemStepMakingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return NewStepMakingViewHolder(binding, onStepDataChangeListener)
    }

    override fun onBindViewHolder(
        holder: NewStepMakingViewHolder,
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
