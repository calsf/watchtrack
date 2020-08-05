package com.chc.watchtrack.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.chc.watchtrack.R
import com.chc.watchtrack.databinding.MoviesFragmentBinding

class MoviesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Get reference to the binding object and inflate the fragment views
        val binding: MoviesFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.movies_fragment, container, false)

        return binding.root
    }
}