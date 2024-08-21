package net.pengcook.android.presentation.core.util

import android.text.InputFilter
import android.text.Spanned

class MinMaxInputFilter(private val min: Int, private val max: Int) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int,
    ): CharSequence? {
        try {
            val input = (dest?.toString() ?: "") + (source?.toString() ?: "")
            val inputVal = input.toInt()
            if (isInRange(min, max, inputVal)) {
                return null
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun isInRange(
        min: Int,
        max: Int,
        value: Int,
    ): Boolean {
        return value in min..max
    }
}
