package com.chc.watchtrack

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat

const val TIME_UNIT = 60
const val MAX_TIME = 99

// Custom EditText to create a time input of 00:00:00 (hours:minutes:seconds)
class EditTextTime : androidx.appcompat.widget.AppCompatEditText {
    private var defaultTextColor: Int
    private var focusedTextColor: Int
    private var hasSet: Boolean = false // False until hours, minutes, and seconds have been set

    // Initialize in the associated fragment.kt file in onCreateView by calling setEditTextTimes
    private var hours: EditTextTime? = null
    private var minutes: EditTextTime? = null
    private var seconds: EditTextTime? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        defaultTextColor = ContextCompat.getColor(context, R.color.defaultText)
        focusedTextColor = ContextCompat.getColor(context, R.color.focusedText)

        // Set up onFocusChangeListener to change text color on focused
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setTextColor(focusedTextColor)
            } else {
                resetTextColor()
            }
        }

        /*
        Reset text color when finished editing
        Focus is kept, if user presses same edit text again, onSelectionChanged will make sure
        that the text returns to focused color
         */
        setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event?.action == KeyEvent.ACTION_DOWN &&
                            event.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    resetTextColor()
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
        if (hasFocus()) {
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
            resetTextColor()
        } else super.dispatchKeyEvent(event)
        return false
    }

    // Add one hour to the hour EditText
    fun addHour() {
        try {
            val currHours = hours?.text.toString().toInt()

            // Only add hour if current hours is less than MAX_TIME
            if (currHours < MAX_TIME) {
                // Setting text will call afterTextChanged, pad length 3 to prevent reset to "00"
                hours?.setText((currHours + 1).toString().padStart(3, '0'))
            }
        } catch (nfe: NumberFormatException) {
            hours?.setText("00")
        }
    }

    // Add one minute to the minute EditText
    fun addMinute() {
        try {
            val currMin = minutes?.text.toString().toInt()

            // If adding a minute makes minutes 60 or more, reset to 00 and add 1 hour
            // Else add one to minute text
            if (currMin + 1 >= TIME_UNIT) {
                minutes?.setText("00")
                addHour()
            } else {
                // Setting text will call afterTextChanged, pad length 3 to prevent reset to "00"
                minutes?.setText((currMin + 1).toString().padStart(3, '0'))
            }

        } catch (nfe: NumberFormatException) {
            minutes?.setText("00")
        }
    }

    // Reset text color to default
    fun resetTextColor()
    {
        setTextColor(defaultTextColor)
    }

    // Set hours, minutes, and seconds EditTextTimes for this EditTextTime
    fun setEditTexts(h: EditTextTime, m: EditTextTime, s: EditTextTime)
    {
        // Assign EditTextTimes only once
        if (!hasSet) {
            hasSet = true

            hours = h
            minutes = m
            seconds = s

            // Set up addTextChangedListener for this EditTextTime
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        // Remove listener so can setText on this EditTextTime without causing loop
                        removeTextChangedListener(this)

                        /*
                    Cursor will always be at the end, show only the last 2 characters
                    This will give behaviour of inserting values from right to left
                     */
                        if (s.length > 2) {
                            setText(s.toString().substring(s.length - 2, s.length))
                        } else {
                            setText("00")
                        }

                        /*
                    If seconds or minutes EditTextTime text goes over TIME_UNIT, carry over 1 time
                    unit and set the current EditTextTime text to the remaining time
                     */
                        try {
                            val num = text.toString().toInt()
                            if (num >= TIME_UNIT) {
                                // The remaining time after carry over
                                val diff = num - TIME_UNIT

                                // Carry over and set current EditTextTime text to remaining time
                                when (this@EditTextTime) {
                                    minutes -> {
                                        addHour()
                                        minutes!!.setText(diff.toString().padStart
                                            (2, '0'))
                                    }
                                    seconds -> {
                                        addMinute()
                                        seconds!!.setText(diff.toString().padStart
                                            (2, '0'))
                                    }
                                }
                            }
                        } catch (nfe: NumberFormatException) {
                            setText("00")
                        }

                        // Set selection to end of current text
                        setSelection(length())

                        // Add back listener
                        addTextChangedListener(this)
                    }
                }

                override fun beforeTextChanged
                            (s: CharSequence?, start: Int, count: Int, after: Int)
                {
                    return
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
                {
                    return
                }
            })
        }
    }

}