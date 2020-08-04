package com.example.watchtrack.shows

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchtrack.*
import com.example.watchtrack.database.WatchDatabase
import com.example.watchtrack.databinding.ShowsFragmentBinding

class ShowsFragment : Fragment() {
    private lateinit var failToast: Toast
    private lateinit var successToast: Toast
    private lateinit var showViewModel: ShowViewModel
    private lateinit var adapter: ShowListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Initialize toasts for successful and unsuccessful show update
        failToast = Toast.makeText(activity, R.string.fail_to_update_show, Toast.LENGTH_SHORT)
        successToast = Toast.makeText(activity, R.string.success_update_show, Toast.LENGTH_LONG)

        // Get reference to the binding object and inflate the fragment views
        val binding: ShowsFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.shows_fragment, container, false)

        // Create an instance of ShowViewModelFactory
        val application = requireNotNull(this.activity).application
        val dataSource = WatchDatabase.getInstance(application).showDatabaseDao
        val showViewModelFactory = ShowViewModelFactory(dataSource, application)

        // Get reference to ShowViewModel
        showViewModel =
            ViewModelProvider(this, showViewModelFactory).get(ShowViewModel::class.java)

        // Set adapter to use for showsList RecyclerView display
        adapter = ShowListAdapter()
        binding.showsList.adapter = adapter

        // ListAdapter detects changes in items and updates the shows in RecyclerView
        showViewModel.shows.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        // Observe update status and show success or fail toast when it changes
        adapter.updateStatus.observe(viewLifecycleOwner, Observer {
            it?.let{
                if (it)
                {
                    successToast.show()
                }
                else
                {
                    failToast.show()
                }
            }
        })

        // Update show when item changes
        adapter.showUpdate.observe(viewLifecycleOwner, Observer {
            it?.let {
                showViewModel.updateShow(it)
            }
        })

        adapter.showsSelected.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty())
            {
                setHasOptionsMenu(false)
                Log.i("YUP", "its null")
            }
            else
            {
                setHasOptionsMenu(true)
                Log.i("YUP", it.toString())
            }

        })


        // Set lifecycle owner to observe LiveData updates
        binding.lifecycleOwner = this

        return binding.root
    }

    /*
     Add the options menu and inflate the menu resource file.
     */
        // "Inflating" a view means taking the layout XML and parsing it to create the view and viewgroup objects
        // from the elements and their attributes specified within, and then adding the hierarchy of those views and viewgroups to the parent ViewGroup
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        deleteSelected()
        return super.onOptionsItemSelected(item)
    }

    // Delete selected items
    private fun deleteSelected() {
        showViewModel.onClear() // Clear for now
        adapter.resetShowsSelected()
    }
}