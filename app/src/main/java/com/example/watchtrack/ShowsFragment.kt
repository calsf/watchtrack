package com.example.watchtrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.watchtrack.databinding.HomeFragmentBinding
import com.example.watchtrack.databinding.ShowsFragmentBinding

class ShowsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Get reference to the binding object and inflate the fragment views
        val binding: ShowsFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.shows_fragment, container, false)

        // TEMP - navigate to addShowFragment
        binding.addShowButton.setOnClickListener{ Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_addShowFragment)}

        return binding.root
    }
}