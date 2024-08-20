import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import net.pengcook.android.databinding.FragmentReportDialogBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.core.model.Recipe
import net.pengcook.android.presentation.detail.dialog.ReportDialogViewModel
import net.pengcook.android.presentation.detail.dialog.ReportDialogViewModelFactory
import net.pengcook.android.presentation.detail.dialog.ReportReasonAdapter

class ReportDialogFragment : DialogFragment() {
    private var _binding: FragmentReportDialogBinding? = null
    private val binding
        get() = _binding!!

    private val recipe: Recipe? by lazy { arguments?.getParcelable("recipe") }

    private val viewModel: ReportDialogViewModel by viewModels {
        val appModule =
            (requireContext().applicationContext as DefaultPengcookApplication).appModule
        ReportDialogViewModelFactory(
            userControlRepository = appModule.userControlRepository,
            recipe = recipe!!,
        )
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
                Toast.makeText(requireContext(), "Recipe Report Success", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(recipe: Recipe): ReportDialogFragment {
            val fragment = ReportDialogFragment()
            val args =
                Bundle().apply {
                    putParcelable("recipe", recipe)
                }
            fragment.arguments = args
            return fragment
        }
    }
}
