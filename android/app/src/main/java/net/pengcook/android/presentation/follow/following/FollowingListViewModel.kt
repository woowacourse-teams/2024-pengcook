package net.pengcook.android.presentation.follow.following

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.pengcook.android.presentation.core.listener.UserManipulationButtonClickListener
import net.pengcook.android.presentation.follow.UserItemClickListener
import javax.inject.Inject

@HiltViewModel
class FollowingListViewModel @Inject constructor() :
    ViewModel(),
    UserManipulationButtonClickListener,
    UserItemClickListener {
    val keyword: MutableLiveData<String> = MutableLiveData("")

    override fun onUserItemSelect(userId: Long) {
    }

    override fun onUserManipulation(userId: Long) {
    }
}
