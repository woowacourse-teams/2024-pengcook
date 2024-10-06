package net.pengcook.android.presentation.setting.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentAccountSettingBinding
import net.pengcook.android.presentation.setting.MenuItem
import net.pengcook.android.presentation.setting.SettingItemRecyclerViewAdapter

@AndroidEntryPoint
class AccountSettingFragment : Fragment() {
    private var _binding: FragmentAccountSettingBinding? = null
    private val binding: FragmentAccountSettingBinding
        get() = _binding!!

    private val viewModel: AccountSettingViewModel by viewModels()

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

                is AccountSettingUiEvent.SignOut -> {
                    FirebaseAuth.getInstance().signOut()
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()

                    val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                    googleSignInClient.signOut()
                    navController.navigate(R.id.action_accountSettingFragment_to_onboardingFragment)
                }

                is AccountSettingUiEvent.DeleteAccount -> {
                    FirebaseAuth.getInstance().currentUser?.delete()
                    navController.navigate(R.id.action_accountSettingFragment_to_onboardingFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
