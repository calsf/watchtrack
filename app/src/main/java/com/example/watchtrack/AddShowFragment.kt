package com.example.watchtrack

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.watchtrack.databinding.AddMovieFragmentBinding
import com.example.watchtrack.databinding.AddShowFragmentBinding

class AddShowFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Get reference to the binding object and inflate the fragment views
        val binding: AddShowFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.add_show_fragment, container, false)

        return binding.root
    }
}