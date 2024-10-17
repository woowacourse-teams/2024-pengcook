package net.pengcook.android.presentation.making

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemStepImageBinding

class StepImageAdapter :
    ListAdapter<RecipeStepImage, StepImageAdapter.ImageViewHolder>(diffUtil),
    ItemMoveListener {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemStepImageBinding =
            ItemStepImageBinding.inflate(layoutInflater, parent, false)
        return ImageViewHolder(binding = binding)
    }

    override fun onBindViewHolder(
        holder: ImageViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    override fun onItemMove(
        from: Int,
        to: Int,
    ) {
        val newList = currentList.toMutableList()
        val item = newList.removeAt(from)
        newList.add(to, item)
        submitList(newList)
    }

    class ImageViewHolder(
        private val binding: ItemStepImageBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: RecipeStepImage) {
            binding.stepImage = image
        }
    }

    companion object {
        val diffUtil: DiffUtil.ItemCallback<RecipeStepImage> =
            object : DiffUtil.ItemCallback<RecipeStepImage>() {
                override fun areItemsTheSame(
                    oldItem: RecipeStepImage,
                    newItem: RecipeStepImage,
                ): Boolean {
                    return oldItem === newItem
                }

                override fun areContentsTheSame(
                    oldItem: RecipeStepImage,
                    newItem: RecipeStepImage,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

interface ItemMoveListener {
    fun onItemMove(
        from: Int,
        to: Int,
    )
}

class ItemMoveCallback(private val listener: ItemMoveListener) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
    0,
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        listener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) {
        // Not To Implement
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int,
    ) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.apply {
                alpha = 0.7f
                scaleX = 1.1f
                scaleY = 1.1f
            }
        }
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.apply {
            alpha = 1.0f
            scaleX = 1.0f
            scaleY = 1.0f
        }
    }
}
