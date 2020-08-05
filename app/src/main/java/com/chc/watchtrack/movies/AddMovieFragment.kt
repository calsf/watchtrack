package com.chc.watchtrack.movies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.chc.watchtrack.R
import com.chc.watchtrack.databinding.AddMovieFragmentBinding

class AddMovieFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Set action bar title
        activity?.title = "Adding Movie"

        // Get reference to the binding object and inflate the fragment views
        val binding: AddMovieFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.add_movie_fragment, container, false)

        return binding.root
    }
}