package net.pengcook.android.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.data.model.SearchData
import net.pengcook.android.databinding.ItemSearchImageBinding

class SearchAdapter : PagingDataAdapter<SearchData, SearchAdapter.SearchViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchImageBinding.inflate(layoutInflater)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    class SearchViewHolder(private val binding: ItemSearchImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchData) {
            binding.imageUrl = item.imageUrl
        }
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<SearchData>() {
                override fun areItemsTheSame(
                    oldItem: SearchData,
                    newItem: SearchData,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: SearchData,
                    newItem: SearchData,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
