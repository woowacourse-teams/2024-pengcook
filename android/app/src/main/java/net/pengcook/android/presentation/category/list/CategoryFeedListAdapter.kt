package net.pengcook.android.presentation.category.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import net.pengcook.android.databinding.ItemFeedBinding
import net.pengcook.android.presentation.core.model.Feed
import net.pengcook.android.presentation.home.FeedRecyclerViewAdapter

class CategoryFeedListAdapter :
    ListAdapter<Feed, FeedRecyclerViewAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FeedRecyclerViewAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFeedBinding.inflate(layoutInflater)
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
            object : DiffUtil.ItemCallback<Feed>() {
                override fun areItemsTheSame(
                    oldItem: Feed,
                    newItem: Feed,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Feed,
                    newItem: Feed,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
