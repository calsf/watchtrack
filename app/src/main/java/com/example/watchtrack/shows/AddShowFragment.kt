package com.example.watchtrack.shows


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.watchtrack.R
import com.example.watchtrack.ShowViewModel
import com.example.watchtrack.ShowViewModelFactory
import com.example.watchtrack.database.WatchDatabase
import com.example.watchtrack.databinding.AddShowFragmentBinding


class AddShowFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Get reference to the binding object and inflate the fragment views
        val binding: AddShowFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.add_show_fragment, container, false)

        // Create an instance of ShowViewModelFactory
        val application = requireNotNull(this.activity).application
        val dataSource = WatchDatabase.getInstance(application).showDatabaseDao
        val showViewModelFactory = ShowViewModelFactory(dataSource, application)

        // Get reference to ShowViewModel
        val showViewModel =
            ViewModelProvider(this, showViewModelFactory).get(ShowViewModel::class.java)

        // Add new show based on input
        binding.addButton.setOnClickListener {
            showViewModel.onAdd(binding.titleInput.text.toString())
        }

        return binding.root
    }
}


