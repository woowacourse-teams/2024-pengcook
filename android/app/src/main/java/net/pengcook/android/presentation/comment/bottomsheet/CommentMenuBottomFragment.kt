package net.pengcook.android.presentation.comment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentCommentMenuBottomBinding
import net.pengcook.android.presentation.DefaultPengcookApplication
import net.pengcook.android.presentation.core.model.Comment

class CommentMenuBottomFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCommentMenuBottomBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: CommentMenuBottomViewModel by viewModels {
        val appModule =
            (requireContext().applicationContext as DefaultPengcookApplication).appModule
        CommentMenuBottomViewModelFactory(
            userControlRepository = appModule.userControlRepository,
        )
    }

    private val selectedComment: Comment? by lazy { arguments?.getParcelable("selected_comment") }

    private val adapter: ReportReasonAdapter by lazy { ReportReasonAdapter(viewModel) }
    private lateinit var callback: CommentMenuCallback

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            setStyle(STYLE_NORMAL, R.style.RoundedBottomSheetDialog)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommentMenuBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.comment = selectedComment

        binding.tvBlock.setOnClickListener {
            val comment = selectedComment ?: return@setOnClickListener
            callback.onBlock(comment)
            dismiss()
        }

        binding.tvDelete.setOnClickListener {
            val comment = selectedComment ?: return@setOnClickListener
            callback.onDelete(comment)
            dismiss()
        }

        binding.adapter = adapter
        binding.vm = viewModel
        viewModel.reportState.observe(viewLifecycleOwner) { state ->
            if (state) {
                Log.d("crong", "$state")
            }
        }

        viewModel.reportReasons.observe(viewLifecycleOwner) { reasons ->
            adapter.submitList(reasons)
        }

        viewModel.selectedReportReason.observe(viewLifecycleOwner) { reason ->
            callback.onReport(selectedComment!!, reason)
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun setCallback(callback: CommentMenuCallback) {
        this.callback = callback
    }

    companion object {
        fun newInstance(
            comment: Comment,
            callback: CommentMenuCallback,
        ): CommentMenuBottomFragment {
            val fragment = CommentMenuBottomFragment()
            val args =
                Bundle().apply {
                    putParcelable("selected_comment", comment)
                }
            fragment.arguments = args
            fragment.setCallback(callback)
            return fragment
        }
    }
}
