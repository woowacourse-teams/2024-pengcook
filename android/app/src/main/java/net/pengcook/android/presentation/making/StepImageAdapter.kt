package net.pengcook.android.presentation.making

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemStepImageBinding
import net.pengcook.android.presentation.making.listener.ItemMoveListener

class StepImageAdapter(
    private val stepItemEventListener: StepItemEventListener,
) :
    ListAdapter<RecipeStepImage, StepImageAdapter.ImageViewHolder>(diffUtil),
        ItemMoveListener {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemStepImageBinding =
            ItemStepImageBinding.inflate(layoutInflater, parent, false)
        return ImageViewHolder(binding = binding, stepItemEventListener = stepItemEventListener)
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
        stepItemEventListener.onOrderChange(newList)
    }

    class ImageViewHolder(
        private val binding: ItemStepImageBinding,
        stepItemEventListener: StepItemEventListener,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.stepItemEventListener = stepItemEventListener
            binding.root.setOnClickListener {
                binding.stepImage?.let {
                    println("stepImage.itemId : ${it.itemId}")
                    stepItemEventListener.onStepImageClick(it)
                }
            }
        }

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
