package net.pengcook.android.presentation.comment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.pengcook.android.R
import net.pengcook.android.databinding.FragmentCommentMenuBottomBinding
import net.pengcook.android.presentation.core.model.Comment

class CommentMenuBottomFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCommentMenuBottomBinding? = null
    private val binding
        get() = _binding!!

    private val selectedComment: Comment? by lazy { arguments?.getParcelable("selected_comment") }
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

        binding.tvReport.setOnClickListener {
            val comment = selectedComment ?: return@setOnClickListener
            callback.onReport(comment)
            dismiss()
        }

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
