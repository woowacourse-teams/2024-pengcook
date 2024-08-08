package net.pengcook.android.presentation.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.launch
import net.pengcook.android.R
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.databinding.FragmentAccountSettingBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.util.Event

class AccountSettingFragment : Fragment() {
    private var _binding: FragmentAccountSettingBinding? = null
    private val binding: FragmentAccountSettingBinding
        get() = _binding!!

    private val viewModel: AccountSettingViewModel by viewModels {
        val application = requireContext().applicationContext as DefaultPengcookApplication
        val sessionRepository = application.appModule.sessionRepository
        AccountSettingViewModelFactory(sessionRepository)
    }

    private val adapter: SettingItemRecyclerViewAdapter by lazy {
        SettingItemRecyclerViewAdapter(
            menus = listOf(MenuItem.SIGN_OUT, MenuItem.DELETE_ACCOUNT),
            settingMenuItemClickListener = viewModel,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAccountSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.adapter = adapter
        binding.viewModel = viewModel

        val dividerItemDecoration =
            MaterialDividerItemDecoration(binding.root.context, LinearLayout.VERTICAL).apply {
                isLastItemDecorated = false
            }

        binding.rvAccountSetting.rvSettingContainer.addItemDecoration(dividerItemDecoration)

        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            val newEvent = event.getContentIfNotHandled() ?: return@observe
            val navController = findNavController()
            when (newEvent) {
                is AccountSettingUiEvent.NavigateBack -> {
                    navController.navigateUp()
                }

                else -> navController.navigate(R.id.action_accountSettingFragment_to_onboardingFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AccountSettingViewModel(
    private val sessionRepository: SessionRepository,
) : ViewModel(),
    SettingMenuItemClickListener,
    AppbarSingleActionEventListener {
    private val _uiEvent: MutableLiveData<Event<AccountSettingUiEvent>> = MutableLiveData()
    val uiEvent: LiveData<Event<AccountSettingUiEvent>>
        get() = _uiEvent

    override fun onSettingMenuItemClick(menuItem: MenuItem) {
        when (menuItem) {
            MenuItem.SIGN_OUT -> signOut()
            MenuItem.DELETE_ACCOUNT -> _uiEvent.value = Event(AccountSettingUiEvent.DeleteAccount)
            else -> Unit
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            sessionRepository.clearAll()
            _uiEvent.value = Event(AccountSettingUiEvent.SignOut)
        }
    }

    override fun onNavigateBack() {
        _uiEvent.value = Event(AccountSettingUiEvent.NavigateBack)
    }
}

sealed interface AccountSettingUiEvent {
    data object SignOut : AccountSettingUiEvent

    data object DeleteAccount : AccountSettingUiEvent

    data object NavigateBack : AccountSettingUiEvent
}

class AccountSettingViewModelFactory(
    private val sessionRepository: SessionRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountSettingViewModel::class.java)) {
            return AccountSettingViewModel(sessionRepository) as T
        }
        throw IllegalArgumentException()
    }
}
