package net.pengcook.android.presentation.comment

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemCommentBinding
import net.pengcook.android.presentation.core.model.Comment

class CommentViewHolder(
    private val binding: ItemCommentBinding,
    private val commentEventHandler: CommentEventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(comment: Comment) {
        binding.comment = comment
        binding.eventHandler = commentEventHandler
    }
}
