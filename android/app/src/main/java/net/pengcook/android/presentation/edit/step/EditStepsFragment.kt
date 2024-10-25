package net.pengcook.android.presentation.edit.step

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import net.pengcook.android.databinding.FragmentEditStepsBinding

@AndroidEntryPoint
class EditStepsFragment : Fragment() {
    private var _binding: FragmentEditStepsBinding? = null
    private val binding: FragmentEditStepsBinding
        get() = _binding!!

    private val args by navArgs<EditStepsFragmentArgs>()

    private val viewModel: EditStepsViewModel by viewModels()

    private val editStepsAdapter: EditStepsAdapter by lazy { EditStepsAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditStepsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
        observeViewModel()
        // viewModel.fetchRecipeSteps()
        binding.vpStepMaking.apply {
            adapter = editStepsAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.appbarEventListener = viewModel
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.exit()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun observeViewModel() {
        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                EditStepsEvent.NavigationEvent -> {
                    viewModel.exit()
                }

                EditStepsEvent.ExitEvent -> {
                    // 나가기
                    findNavController().navigateUp()
                }

                EditStepsEvent.TempSaveEvent -> {
                    // 임시저장
                    viewModel.saveData()
                    Toast.makeText(requireContext(), "임시저장되었습니다.", Toast.LENGTH_SHORT).show()
                }

                EditStepsEvent.OnFetchComplete -> {
                    // 데이터 로딩 완료
                    println("EditStepsFragment : ${args.sequence}")
                    binding.vpStepMaking.setCurrentItem(args.sequence - 1, false)
                }

                EditStepsEvent.OnSaveFailure -> {
                    Toast.makeText(requireContext(), "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.steps.observe(viewLifecycleOwner) { steps ->
            println("EditStepsFragment : $steps")
            editStepsAdapter.submitList(steps)
            binding.vpStepMaking.setCurrentItem(args.sequence - 1, false)
        }
    }
}
