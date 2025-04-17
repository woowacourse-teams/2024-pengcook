package net.pengcook.android.presentation.otherprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.ui.theme.PengCookTheme
import javax.inject.Inject

@AndroidEntryPoint
class OtherProfileFragment : Fragment() {
    private val args: OtherProfileFragmentArgs by navArgs()
    private val userId: Long by lazy { args.userId }

    @Inject
    lateinit var viewModelFactory: OtherProfileViewModelFactory

    private val viewModel: OtherProfileViewModel by viewModels {
        OtherProfileViewModel.provideFactory(viewModelFactory, userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                PengCookTheme {
                    OtherProfileScreenRoot(
                        viewModel = viewModel,
                        navigateBack = { navigateBack() },
                        navigateToFollowList = { navigateToFollowList() },
                    )
                }
            }
        }

    private fun navigateBack() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun navigateToFollowList() {
        val action =
            OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowList2Fragment(userId)
        findNavController().navigate(action)
    }
}
