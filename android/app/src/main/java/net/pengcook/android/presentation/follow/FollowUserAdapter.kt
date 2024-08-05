package net.pengcook.android.presentation.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.pengcook.android.databinding.ItemUserFollowBinding
import net.pengcook.android.presentation.core.listener.UserManipulationButtonClickListener
import net.pengcook.android.presentation.core.model.User

class FollowUserAdapter(
    private val userManipulationButtonClickListener: UserManipulationButtonClickListener,
    private val userItemClickListener: UserItemClickListener,
) : PagingDataAdapter<User, FollowUserViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FollowUserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemUserFollowBinding.inflate(layoutInflater, parent, false)
        return FollowUserViewHolder(
            binding,
            userManipulationButtonClickListener,
            userItemClickListener,
        )
    }

    override fun onBindViewHolder(
        holder: FollowUserViewHolder,
        position: Int,
    ) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<User>() {
                override fun areItemsTheSame(
                    oldItem: User,
                    newItem: User,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: User,
                    newItem: User,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
