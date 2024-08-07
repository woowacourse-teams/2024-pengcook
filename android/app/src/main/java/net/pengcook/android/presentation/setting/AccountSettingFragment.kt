package net.pengcook.android.presentation.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.divider.MaterialDividerItemDecoration
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentAccountSettingBinding
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener

class AccountSettingFragment :
    Fragment(),
    SettingMenuItemClickListener,
    AppbarSingleActionEventListener {
    private var _binding: FragmentAccountSettingBinding? = null
    private val binding: FragmentAccountSettingBinding
        get() = _binding!!

    private val adapter: SettingItemRecyclerViewAdapter by lazy {
        SettingItemRecyclerViewAdapter(
            menus = listOf(MenuItem.SIGN_OUT, MenuItem.DELETE_ACCOUNT),
            settingMenuItemClickListener = this,
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
        binding.appbarEventListener = this

        val dividerItemDecoration =
            MaterialDividerItemDecoration(binding.root.context, LinearLayout.VERTICAL).apply {
                isLastItemDecorated = false
            }

        binding.rvAccountSetting.rvSettingContainer.addItemDecoration(dividerItemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSettingMenuItemClick(menuItem: MenuItem) {
        when (menuItem) {
            MenuItem.SIGN_OUT -> {
                findNavController().navigate(R.id.action_accountSettingFragment_to_onboardingFragment)
            }
            MenuItem.DELETE_ACCOUNT -> {
            }
            else -> Unit
        }
    }

    override fun onNavigateBack() {
        findNavController().navigateUp()
    }
}
