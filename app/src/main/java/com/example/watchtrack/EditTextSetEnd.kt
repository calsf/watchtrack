package com.example.watchtrack

import android.content.Context
import android.util.AttributeSet

class EditTextSetEnd(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatEditText(context, attrs) {
    // Always set cursor position to the end of the text
    override fun onSelectionChanged(start: Int, end: Int) {
        super.onSelectionChanged(start, end)
        setSelection(text!!.length, text!!.length)
    }
}