package com.example.watchtrack

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.watchtrack.databinding.AddMovieFragmentBinding
import com.example.watchtrack.databinding.AddShowFragmentBinding


class AddShowFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Get reference to the binding object and inflate the fragment views
        val binding: AddShowFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.add_show_fragment, container, false)

//        binding.seconds.setOnFocusChangeListener { view, hasFocus ->
//            if (hasFocus) {
//                binding.seconds.setTextColor(Color.RED)
//            }
//            else {
//                binding.seconds.setTextColor(Color.BLACK)
//            }
//        }
//
//        binding.seconds.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                s?.let {
//                    binding.seconds.removeTextChangedListener(this)
//                    if (s.length > 2) {
//                        binding.seconds.setText(s.toString().substring(s.length - 2, s.length))
//                    }
//                    else
//                    {
//                        binding.seconds.setText("00")
//                    }
//                    binding.seconds.setSelection(binding.seconds.length())
//                    binding.seconds.addTextChangedListener(this)
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                return
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                return
//            }
//
//        })

        return binding.root
    }
}


