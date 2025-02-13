package net.pengcook.android.presentation.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentSettingBinding
import net.pengcook.android.presentation.core.listener.AppbarSingleActionEventListener
import net.pengcook.android.presentation.core.util.AnalyticsLogging
import net.pengcook.android.presentation.setting.model.Setting

@AndroidEntryPoint
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
                listOf(MenuItem.PRIVACY_POLICY, MenuItem.TERMS_OF_USE, MenuItem.MY_COMMENTS, MenuItem.ACCOUNT),
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
        AnalyticsLogging.init(requireContext()) // Firebase Analytics 초기화
        AnalyticsLogging.viewLogEvent("Setting")
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
            MenuItem.MY_COMMENTS -> findNavController().navigate(R.id.action_settingFragment_to_myCommentFragment)
            MenuItem.COMMENTS -> {}
            MenuItem.BLOCKED -> {}
            MenuItem.LANGUAGE -> {}
            MenuItem.PRIVACY_POLICY -> navigateToBrowser("https://pengcook.net/privacy-policy")
            MenuItem.TERMS_OF_USE -> navigateToBrowser("https://pengcook.net/terms-of-service")
            MenuItem.ACCOUNT ->
                findNavController().navigate(R.id.action_settingFragment_to_accountSettingFragment)

            else -> Unit
        }
    }

    override fun onNavigateBack() {
        findNavController().navigateUp()
    }

    private fun navigateToBrowser(uriString: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).also(::startActivity)
    }
}
