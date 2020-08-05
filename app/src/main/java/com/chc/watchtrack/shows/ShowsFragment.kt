package com.chc.watchtrack.shows

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chc.watchtrack.DeletePopup
import com.chc.watchtrack.R
import com.chc.watchtrack.database.Show
import com.chc.watchtrack.database.WatchDatabase
import com.chc.watchtrack.databinding.ShowsFragmentBinding


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
        val showViewModelFactory =
            ShowViewModelFactory(
                dataSource,
                application
            )

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

        /*
        If no shows are selected, hide options menu and change title to app name
        Else there are shows selected, show options menu and change title to amount selected
         */
        adapter.showsSelected.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty())
            {
                setHasOptionsMenu(false)

                // Hide clear icon next to title
                (activity as AppCompatActivity?)!!.supportActionBar?.
                setDisplayHomeAsUpEnabled(false)

                activity?.setTitle(R.string.app_name)
            }
            else
            {
                setHasOptionsMenu(true)

                // Show clear icon next to title
                (activity as AppCompatActivity?)!!.supportActionBar?.
                setDisplayHomeAsUpEnabled(true)

                activity?.title = "${it.size} Selected"
            }
        })

        // Set lifecycle owner to observe LiveData updates
        binding.lifecycleOwner = this

        return binding.root
    }

    // Add the delete menu and inflate the menu resource file
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.selected_menu, menu)

        // Replace up with clear icon
        (activity as AppCompatActivity?)!!.supportActionBar?.
        setHomeAsUpIndicator(R.drawable.ic_clear)
    }

    /*
    Show delete popup to confirm deletion of selected items
    Pass in the delete action
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.delete -> {
                val deletePopup = DeletePopup()
                deletePopup.showDeletePopup(requireActivity()) { deleteSelected() }
            }
            R.id.select_all -> {
                adapter.selectedAll = true
                adapter.notifyDataSetChanged()
            }
            android.R.id.home -> {
                adapter.selectedAll = false
                adapter.resetShowsSelected()
                adapter.notifyDataSetChanged()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Delete selected show items
    private fun deleteSelected () {
        val list: MutableList<Show> = ArrayList()
        list.addAll(adapter.showsSelected.value!!)
        showViewModel.deleteShows(list)

        // Clear the list of selected shows
        adapter.resetShowsSelected()

        // Reset title and hide options menu
        setHasOptionsMenu(false)
        activity?.setTitle(R.string.app_name)
    }

}