package net.pengcook.android.presentation.follow

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.R
import net.pengcook.android.databinding.ItemUserFollowBinding
import net.pengcook.android.presentation.core.listener.UserManipulationButtonClickListener
import net.pengcook.android.presentation.core.model.User
import net.pengcook.android.presentation.follow.follower.FollowerListFragment
import net.pengcook.android.presentation.follow.following.FollowingListFragment

class FollowUserViewHolder(
    private val binding: ItemUserFollowBinding,
    userManipulationButtonClickListener: UserManipulationButtonClickListener,
    userItemClickListener: UserItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.followButtonClickListener = userManipulationButtonClickListener
        binding.userItemClickListener = userItemClickListener
        when (binding.lifecycleOwner) {
            is FollowerListFragment -> {
                binding.buttonText =
                    binding.root.context.getString(R.string.profile_follower_button)
            }

            is FollowingListFragment -> {
                binding.buttonText =
                    binding.root.context.getString(R.string.profile_following_button)
            }
        }
    }

    fun bind(user: User) {
        binding.user = user
    }
}
