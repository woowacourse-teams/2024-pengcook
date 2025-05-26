package net.pengcook.android.presentation.search

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView

class SearchActionListener : TextView.OnEditorActionListener {
    override fun onEditorAction(
        v: TextView?,
        actionId: Int,
        event: KeyEvent?,
    ): Boolean {
        return actionId == EditorInfo.IME_ACTION_SEARCH
    }
}
