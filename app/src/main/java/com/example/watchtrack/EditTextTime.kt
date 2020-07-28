package com.example.watchtrack

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat

// Custom EditText for time input
class EditTextTime : androidx.appcompat.widget.AppCompatEditText {
    private var defaultTextColor : Int
    private var focusedTextColor: Int

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        defaultTextColor = ContextCompat.getColor(context, R.color.defaultText)
        focusedTextColor = ContextCompat.getColor(context, R.color.focusedText)

        // Set up addTextChangedListener to insert values from right to left with a max of 2 characters
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    removeTextChangedListener(this)
                    if (s.length > 2) {
                        setText(s.toString().substring(s.length - 2, s.length))
                    } else {
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
                setTextColor(focusedTextColor)
            } else {
                setTextColor(defaultTextColor)
            }
        }

        /* Reset text color when finished editing
        Focus is kept, if user presses same edit text again, onSelectionChanged will make sure
        that the text returns to focused color
        * */
        setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event?.action == KeyEvent.ACTION_DOWN &&
                    event?.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    setTextColor(defaultTextColor)
                    return false
                }
                return false
            }
        })
    }

    // Always set cursor position to the end of the text
    override fun onSelectionChanged(start: Int, end: Int) {
        super.onSelectionChanged(start, end)
        // Makes sure text color is focused color if selected again and already focused
        if (hasFocus()){
            setTextColor(focusedTextColor)
        }
        setSelection(text!!.length, text!!.length)
    }

    /* If back pressed while input is open, reset text color
        Focus is kept, if user presses same edit text again, onSelectionChanged will make sure
        that the text returns to focused color
        * */
    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            setTextColor(defaultTextColor)
        } else super.dispatchKeyEvent(event)
        return false
    }

}