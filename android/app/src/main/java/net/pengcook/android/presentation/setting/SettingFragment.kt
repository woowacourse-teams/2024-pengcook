package net.pengcook.android.presentation.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.pengcook.android.databinding.FragmentSettingBinding
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener

class SettingFragment :
    Fragment(),
    SettingMenuItemClickListener,
    AppbarSingleActionEventListener {
    private var _binding: FragmentSettingBinding? = null
    private val binding: FragmentSettingBinding
        get() = _binding!!
    private val settings: List<Setting> =
        listOf(
            Setting(
                listOf(MenuItem.LIKES, MenuItem.COMMENTS),
            ),
            Setting(
                listOf(MenuItem.BLOCKED, MenuItem.LANGUAGE),
            ),
            Setting(
                listOf(MenuItem.PRIVACY_POLICY, MenuItem.TERMS_OF_USE, MenuItem.ACCOUNT),
            ),
        )
    private val adapter: SettingRecyclerViewAdapter by lazy {
        SettingRecyclerViewAdapter(settings, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.adapter = adapter
        binding.appbarActionEventListener = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSettingMenuItemClick(menuItem: MenuItem) {
        when (menuItem) {
            MenuItem.LIKES -> {}
            MenuItem.COMMENTS -> {}
            MenuItem.BLOCKED -> {}
            MenuItem.LANGUAGE -> {}
            MenuItem.PRIVACY_POLICY -> {}
            MenuItem.TERMS_OF_USE -> {}
            MenuItem.ACCOUNT -> {}
        }
    }

    override fun onNavigateBack() {
    }
}
