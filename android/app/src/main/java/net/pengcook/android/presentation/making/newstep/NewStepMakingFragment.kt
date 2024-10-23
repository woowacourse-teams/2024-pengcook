package net.pengcook.android.presentation.making.newstep

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
import net.pengcook.android.databinding.FragmentNewStepMakingBinding

@AndroidEntryPoint
class NewStepMakingFragment : Fragment() {
    private var _binding: FragmentNewStepMakingBinding? = null
    private val binding: FragmentNewStepMakingBinding
        get() = _binding!!

    private val args by navArgs<NewStepMakingFragmentArgs>()

    private val viewModel: NewStepMakingViewModel by viewModels()

    private val newStepMakingAdapter: NewStepMakingAdapter by lazy { NewStepMakingAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewStepMakingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
        observeViewModel()
        viewModel.fetchRecipeSteps()
        binding.vpStepMaking.apply {
            adapter = newStepMakingAdapter
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
        viewModel.newStepMakingEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                NewStepMakingEvent.NavigationEvent -> {
                    viewModel.exit()
                }

                NewStepMakingEvent.ExitEvent -> {
                    // 나가기
                    findNavController().navigateUp()
                }

                NewStepMakingEvent.TempSaveEvent -> {
                    // 임시저장
                    viewModel.saveData()
                    Toast.makeText(requireContext(), "임시저장되었습니다.", Toast.LENGTH_SHORT).show()
                }

                NewStepMakingEvent.OnFetchComplete -> {
                    // 데이터 로딩 완료
                    binding.vpStepMaking.setCurrentItem(args.sequence - 1, false)
                }
            }
        }

        viewModel.steps.observe(viewLifecycleOwner) { steps ->
            newStepMakingAdapter.submitList(steps)
        }
    }
}
