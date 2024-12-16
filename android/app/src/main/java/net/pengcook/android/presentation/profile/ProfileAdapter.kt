package net.pengcook.android.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemProfileButtonBinding
import net.pengcook.android.databinding.ItemProfileDescriptionBinding
import net.pengcook.android.databinding.ItemProfileFeedBinding

class ProfileAdapter(
    private val profileButtonClickListener: ProfileButtonClickListener,
    private val profileFeedClickListener: ProfileFeedClickListener,
) : PagingDataAdapter<ProfileViewItem, RecyclerView.ViewHolder>(diffUtils) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ProfileViewItem.VIEW_TYPE_PROFILE_DESC -> {
                val binding = ItemProfileDescriptionBinding.inflate(inflater, parent, false)
                return ProfileDescriptionViewHolder(binding)
            }

            ProfileViewItem.VIEW_TYPE_PROFILE_BUTTONS -> {
                val binding = ItemProfileButtonBinding.inflate(inflater, parent, false)
                return ProfileButtonsViewHolder(
                    binding = binding,
                    profileButtonClickListener = profileButtonClickListener,
                )
            }

            ProfileViewItem.VIEW_TYPE_FEEDS -> {
                val binding = ItemProfileFeedBinding.inflate(inflater, parent, false)
                return ProfileFeedViewHolder(binding, profileFeedClickListener)
            }

            else -> throw RuntimeException("Illegal view type exists.")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        when (holder) {
            is ProfileDescriptionViewHolder -> {
                if (item is ProfileViewItem.ProfileDescription) {
                    holder.bind(item.profile)
                }
            }

            is ProfileFeedViewHolder -> {
                if (item is ProfileViewItem.ProfileFeeds) {
                    holder.bind(item.recipe)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> ProfileViewItem.VIEW_TYPE_PROFILE_DESC
            1 -> ProfileViewItem.VIEW_TYPE_PROFILE_BUTTONS
            else -> ProfileViewItem.VIEW_TYPE_FEEDS
        }

    companion object {
        val diffUtils =
            object : DiffUtil.ItemCallback<ProfileViewItem>() {
                override fun areItemsTheSame(
                    oldItem: ProfileViewItem,
                    newItem: ProfileViewItem,
                ): Boolean {
                    val isProfileDescriptionSame =
                        oldItem is ProfileViewItem.ProfileDescription && newItem is ProfileViewItem.ProfileDescription
                    val isProfileButtonSame =
                        oldItem is ProfileViewItem.ProfileButtons && newItem is ProfileViewItem.ProfileButtons
                    val isProfileFeedSame =
                        oldItem is ProfileViewItem.ProfileFeeds &&
                            newItem is ProfileViewItem.ProfileFeeds &&
                            (oldItem.recipe.recipeId == newItem.recipe.recipeId)
                    return isProfileDescriptionSame || isProfileButtonSame || isProfileFeedSame
                }

                override fun areContentsTheSame(
                    oldItem: ProfileViewItem,
                    newItem: ProfileViewItem,
                ): Boolean = oldItem == newItem
            }
    }
}
