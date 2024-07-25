package net.pengcook.android.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemFeedBinding
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.home.listener.FeedItemEventListener

class FeedRecyclerViewAdapter(private val eventListener: FeedItemEventListener) :
    PagingDataAdapter<Recipe, FeedRecyclerViewAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFeedBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class ViewHolder(private val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.recipe = item
            binding.executePendingBindings()
        }
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
