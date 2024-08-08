package net.pengcook.android.presentation.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import net.pengcook.android.databinding.ItemCommentBinding
import net.pengcook.android.presentation.core.model.Comment

class CommentAdapter(
    private val commentEventHandler: CommentEventHandler,
) : ListAdapter<Comment, CommentViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(layoutInflater, parent, false)
        return CommentViewHolder(binding, commentEventHandler)
    }

    override fun onBindViewHolder(
        holder: CommentViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<Comment>() {
                override fun areItemsTheSame(
                    oldItem: Comment,
                    newItem: Comment,
                ): Boolean = oldItem.commentId == newItem.commentId

                override fun areContentsTheSame(
                    oldItem: Comment,
                    newItem: Comment,
                ): Boolean = oldItem == newItem
            }
    }
}
