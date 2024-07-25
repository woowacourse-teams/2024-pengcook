package net.pengcook.android.presentation.category

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemCategoryBinding

class CategoryViewHolder(
    private val binding: ItemCategoryBinding,
    categoryEventListener: CategoryEventListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.categoryEventListener = categoryEventListener
    }

    fun bind(category: Category) {
        binding.category = category
    }
}
