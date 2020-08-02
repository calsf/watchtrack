package com.example.watchtrack.shows

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.watchtrack.ShowViewModel
import com.example.watchtrack.ShowViewModelFactory
import com.example.watchtrack.R
import com.example.watchtrack.ShowListAdapter
import com.example.watchtrack.database.WatchDatabase
import com.example.watchtrack.databinding.ShowsFragmentBinding

class ShowsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Get reference to the binding object and inflate the fragment views
        val binding: ShowsFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.shows_fragment, container, false)

        // Create an instance of ShowViewModelFactory
        val application = requireNotNull(this.activity).application
        val dataSource = WatchDatabase.getInstance(application).showDatabaseDao
        val showViewModelFactory = ShowViewModelFactory(dataSource, application)

        // Get reference to ShowViewModel
        val showViewModel =
            ViewModelProvider(this, showViewModelFactory).get(ShowViewModel::class.java)

        // Set adapter to use for showsList RecyclerView display
        val adapter = ShowListAdapter()
        binding.showsList.adapter = adapter

        // ListAdapter detects changes in items and updates the shows in RecyclerView
        showViewModel.shows.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        // Set lifecycle owner to observe LiveData updates
        binding.lifecycleOwner = this

        return binding.root
    }
}