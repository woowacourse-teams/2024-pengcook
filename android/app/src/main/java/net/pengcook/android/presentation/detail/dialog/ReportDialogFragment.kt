package net.pengcook.android.presentation.detail.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentReportDialogBinding
import net.pengcook.android.presentation.core.model.Recipe
import javax.inject.Inject

@AndroidEntryPoint
class ReportDialogFragment : DialogFragment() {
    private var _binding: FragmentReportDialogBinding? = null
    private val binding
        get() = _binding!!

    private val recipe: Recipe? by lazy { arguments?.getParcelable("recipe") }

    @Inject
    lateinit var viewModelFactory: ReportDialogViewModelFactory

    private val viewModel: ReportDialogViewModel by viewModels {
        ReportDialogViewModel.provideFactory(viewModelFactory, recipe!!) //TODO Add NullSafety
    }
    private val adapter: ReportReasonAdapter by lazy { ReportReasonAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReportDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.adapter = adapter
        binding.reportEventHandler = viewModel

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.reportReasons.observe(viewLifecycleOwner) { reasons ->
            adapter.submitList(reasons)
        }

        viewModel.reportState.observe(viewLifecycleOwner) { state ->
            if (state) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.recipe_report_success),
                    Toast.LENGTH_SHORT,
                ).show()
                dismiss()
            }
        }
    }

    companion object {
        private const val ARGS_KEY = "recipe"

        fun newInstance(recipe: Recipe): ReportDialogFragment {
            val fragment = ReportDialogFragment()
            val args =
                Bundle().apply {
                    putParcelable(ARGS_KEY, recipe)
                }
            fragment.arguments = args
            return fragment
        }
    }
}
