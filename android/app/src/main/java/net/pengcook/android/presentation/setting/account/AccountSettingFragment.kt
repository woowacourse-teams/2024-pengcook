package net.pengcook.android.presentation.setting.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.divider.MaterialDividerItemDecoration
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentAccountSettingBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.setting.MenuItem
import net.pengcook.android.presentation.setting.SettingItemRecyclerViewAdapter

class AccountSettingFragment : Fragment() {
    private var _binding: FragmentAccountSettingBinding? = null
    private val binding: FragmentAccountSettingBinding
        get() = _binding!!

    private val viewModel: AccountSettingViewModel by viewModels {
        val application = requireContext().applicationContext as DefaultPengcookApplication
        val sessionRepository = application.appModule.sessionRepository
        val authorizationRepository = application.appModule.authorizationRepository
        AccountSettingViewModelFactory(sessionRepository, authorizationRepository)
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
