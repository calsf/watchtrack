package com.example.watchtrack.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.watchtrack.R
import com.example.watchtrack.databinding.MoviesFragmentBinding

class MoviesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Set action bar title
        activity?.title = "Adding Movie"

        // Get reference to the binding object and inflate the fragment views
        val binding: MoviesFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.movies_fragment, container, false)

        return binding.root
    }
}