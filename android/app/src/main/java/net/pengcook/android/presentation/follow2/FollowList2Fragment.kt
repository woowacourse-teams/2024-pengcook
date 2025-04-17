package net.pengcook.android.presentation.follow2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.ui.theme.PengCookTheme
import javax.inject.Inject

@AndroidEntryPoint
class FollowList2Fragment : Fragment() {
    private val args: FollowList2FragmentArgs by navArgs()
    private val userId: Long by lazy { args.userId }

    @Inject
    lateinit var viewModelFactory: FollowListViewModelFactory

    private val viewModel: FollowListViewModel by viewModels {
        FollowListViewModel.provideFactory(viewModelFactory, userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                PengCookTheme {
                    FollowListScreenRoot(
                        viewModel = viewModel,
                        navigateBack = { navigateBack() },
                    )
                }
            }
        }

    private fun navigateBack() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}
