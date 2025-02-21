package net.pengcook.android.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.pengcook.android.databinding.ItemFeedBinding
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.home.holder.FeedViewHolder
import net.pengcook.android.presentation.home.listener.FeedItemEventListener

class FeedRecyclerViewAdapter(
    private val homeEventListener: HomeEventListener,
    private val eventListener: FeedItemEventListener,
) :
    PagingDataAdapter<RecipeForList, FeedViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFeedBinding.inflate(inflater, parent, false)
        return FeedViewHolder(
            binding = binding,
            homeEventListener = homeEventListener,
            eventListener = eventListener,
        )
    }

    override fun onBindViewHolder(
        holder: FeedViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        val diffCallback =
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
