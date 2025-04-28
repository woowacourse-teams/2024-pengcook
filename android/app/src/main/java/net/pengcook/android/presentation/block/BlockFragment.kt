package net.pengcook.android.presentation.block

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.ui.theme.PengCookTheme

@AndroidEntryPoint
class BlockFragment : Fragment() {
    private val viewModel: BlockViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                PengCookTheme {
                    BlockScreenRoot(
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
