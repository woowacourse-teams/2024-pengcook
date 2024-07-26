package net.pengcook.android.presentation.category.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.pengcook.android.databinding.ItemFeedBinding
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.home.FeedRecyclerViewAdapter

class CategoryFeedListAdapter :
    PagingDataAdapter<Recipe, FeedRecyclerViewAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FeedRecyclerViewAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFeedBinding.inflate(layoutInflater, parent, false)
        return FeedRecyclerViewAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FeedRecyclerViewAdapter.ViewHolder,
        position: Int,
    ) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    companion object {
        val diffCallback =
            object : DiffUtil.ItemCallback<Recipe>() {
                override fun areItemsTheSame(
                    oldItem: Recipe,
                    newItem: Recipe,
                ): Boolean {
                    return oldItem.recipeId == newItem.recipeId
                }

                override fun areContentsTheSame(
                    oldItem: Recipe,
                    newItem: Recipe,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
