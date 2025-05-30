package net.pengcook.android.presentation.step

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import net.pengcook.android.databinding.DialogTimerSettingBinding

class TimerSettingDialogFragment : DialogFragment() {
    private var _binding: DialogTimerSettingBinding? = null
    private val binding
        get() = _binding!!

    interface TimerSettingListener {
        fun onTimeSet(
            minutes: Int,
            seconds: Int,
        )
    }

    var listener: TimerSettingListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogTimerSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupNumberPickers()

        binding.btnConfirm.setOnClickListener {
            val minutes = binding.npMinutes.value
            val seconds = binding.npSeconds.value
            listener?.onTimeSet(minutes, seconds)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupNumberPickers() {
        binding.npMinutes.minValue = MIN_VALUE
        binding.npMinutes.maxValue = MAX_VALUE
        binding.npMinutes.wrapSelectorWheel = true

        binding.npSeconds.minValue = MIN_VALUE
        binding.npSeconds.maxValue = MAX_VALUE
        binding.npSeconds.wrapSelectorWheel = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MIN_VALUE = 0
        private const val MAX_VALUE = 59
    }
}
