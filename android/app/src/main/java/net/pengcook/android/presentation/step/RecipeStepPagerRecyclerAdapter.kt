package net.pengcook.android.presentation.step

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemStepRecipeBinding
import net.pengcook.android.presentation.core.model.RecipeStep

class RecipeStepPagerRecyclerAdapter(
    private var pageList: List<RecipeStep> = emptyList(),
) : RecyclerView.Adapter<RecipeStepViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecipeStepViewHolder {
        val binding = ItemStepRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeStepViewHolder(binding)
    }

    override fun getItemCount(): Int = pageList.size

    override fun onBindViewHolder(
        holder: RecipeStepViewHolder,
        position: Int,
    ) {
        holder.bind(pageList[position])
    }

    fun updateList(list: List<RecipeStep>) {
        pageList = list
        notifyItemInserted(pageList.size)
    }
}
