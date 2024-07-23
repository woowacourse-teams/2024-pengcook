package net.pengcook.android.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.pengcook.android.databinding.ItemFeedBinding
import net.pengcook.android.listner.FeedItemEventListener
import net.pengcook.android.model.Feed

class FeedRecyclerViewAdapter(private val eventListener: FeedItemEventListener) :
    PagingDataAdapter<Feed, FeedRecyclerViewAdapter.ViewHolder>(diffCallback) {
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
        fun bind(item: Feed) {
            binding.feed = item
            binding.executePendingBindings()
        }
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

@BindingAdapter("app:image")
fun ImageView.image(url: String?) {
    if (url == null) return
    Glide.with(this.context)
        .load(url)
        .into(this)
}
