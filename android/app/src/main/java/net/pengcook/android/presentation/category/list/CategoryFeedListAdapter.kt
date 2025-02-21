package net.pengcook.android.presentation.category.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.pengcook.android.databinding.ItemFeedBinding
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.home.listener.FeedItemEventListener

class CategoryFeedListAdapter(
    private val eventListener: FeedItemEventListener,
) :
    PagingDataAdapter<RecipeForList, CategoryFeedViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CategoryFeedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFeedBinding.inflate(layoutInflater, parent, false)
        return CategoryFeedViewHolder(binding, eventListener)
    }

    override fun onBindViewHolder(
        holder: CategoryFeedViewHolder,
        position: Int,
    ) {
        val item = getItem(position) ?: return
        holder.bind(item)
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
