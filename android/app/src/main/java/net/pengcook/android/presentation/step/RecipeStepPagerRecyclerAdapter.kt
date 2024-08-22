package net.pengcook.android.presentation.step

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import net.pengcook.android.databinding.ItemStepRecipeBinding
import net.pengcook.android.presentation.core.model.RecipeStep

class RecipeStepPagerRecyclerAdapter(
    private var pageList: List<RecipeStep> = emptyList(),
    private val viewPager2: ViewPager2,
) : RecyclerView.Adapter<RecipeStepViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecipeStepViewHolder {
        val binding =
            ItemStepRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeStepViewHolder(binding)
    }

    override fun getItemCount(): Int = pageList.size

    override fun onBindViewHolder(
        holder: RecipeStepViewHolder,
        position: Int,
    ) {
        holder.bind(pageList[position], createOnClickListener())
    }

    private fun createOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            val currentItem = viewPager2.currentItem
            if (currentItem < itemCount - 1) {
                viewPager2.currentItem = currentItem + 1
            }
        }
    }

    fun updateList(list: List<RecipeStep>) {
        pageList = list
        notifyItemInserted(pageList.size)
    }
}
