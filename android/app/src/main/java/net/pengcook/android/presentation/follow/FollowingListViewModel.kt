package net.pengcook.android.presentation.follow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.pengcook.android.presentation.core.listener.UserManipulationButtonClickListener

class FollowingListViewModel :
    ViewModel(),
    UserManipulationButtonClickListener,
    UserItemClickListener {
    val keyword: MutableLiveData<String> = MutableLiveData("")

    override fun onUserItemSelect(userId: Long) {
    }

    override fun onUserManipulation(userId: Long) {
    }
}
