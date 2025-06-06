package net.pengcook.android.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemSearchImageBinding
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.home.listener.FeedItemEventListener

class SearchAdapter(
    private val feedItemEventListener: FeedItemEventListener,
) : PagingDataAdapter<RecipeForList, SearchAdapter.SearchViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchImageBinding.inflate(layoutInflater)
        return SearchViewHolder(binding, feedItemEventListener)
    }

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    class SearchViewHolder(
        private val binding: ItemSearchImageBinding,
        feedItemEventListener: FeedItemEventListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.feedItemEventListener = feedItemEventListener
        }

        fun bind(recipe: RecipeForList) {
            binding.recipe = recipe
        }
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<RecipeForList>() {
                override fun areItemsTheSame(
                    oldItem: RecipeForList,
                    newItem: RecipeForList,
                ): Boolean {
                    return oldItem.recipeId == newItem.recipeId
                }

                override fun areContentsTheSame(
                    oldItem: RecipeForList,
                    newItem: RecipeForList,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
