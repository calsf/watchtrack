package com.example.watchtrack

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ContextMenu

// Custom EditText for time input
class EditTextTime
    : androidx.appcompat.widget.AppCompatEditText {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // Set up addTextChangedListener to insert values from right to left with a max of 2 characters
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    removeTextChangedListener(this)
                    if (s.length > 2) {
                        setText(s.toString().substring(s.length - 2, s.length))
                    }
                    else
                    {
                        setText("00")
                    }
                    setSelection(length())
                    addTextChangedListener(this)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })

        // Set up onFocusChangeListener to change text color on focused
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setTextColor(Color.GREEN)
            }
            else {
                setTextColor(Color.BLACK)
            }
        }
    }


    // Always set cursor position to the end of the text
    override fun onSelectionChanged(start: Int, end: Int) {
        super.onSelectionChanged(start, end)
        setSelection(text!!.length, text!!.length)
    }




}