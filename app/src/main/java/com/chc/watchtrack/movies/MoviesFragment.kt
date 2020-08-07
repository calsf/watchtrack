package com.chc.watchtrack.movies

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chc.watchtrack.DeletePopup
import com.chc.watchtrack.R
import com.chc.watchtrack.database.MovieEntity
import com.chc.watchtrack.database.WatchDatabase
import com.chc.watchtrack.databinding.MoviesFragmentBinding

class MoviesFragment : Fragment() {
    private lateinit var failToast: Toast
    private lateinit var successToast: Toast
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var adapter: MovieListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Initialize toasts for successful and unsuccessful movie update
        failToast = Toast.makeText(activity, R.string.fail_to_update_movie, Toast.LENGTH_SHORT)
        successToast = Toast.makeText(activity, R.string.success_update_movie, Toast.LENGTH_LONG)

        // Get reference to the binding object and inflate the fragment views
        val binding: MoviesFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.movies_fragment, container, false)

        // Create an instance of MovieViewModelFactory
        val application = requireNotNull(this.activity).application
        val dataSource = WatchDatabase.getInstance(application).movieDatabaseDao
        val movieViewModelFactory =
            MovieViewModelFactory(
                dataSource,
                application
            )

        // Get reference to MovieViewModel
        movieViewModel =
            ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        // Set adapter to use for moviesList RecyclerView display
        adapter = MovieListAdapter()
        binding.moviesList.adapter = adapter

        // Init adapter selectedItems
        adapter.selectedItems = ArrayList()

        // Init moviesSelected
        movieViewModel.initMoviesSelected()

        // Check selected to set action bar title
        checkSelected(movieViewModel.moviesSelected.value)

        // Set adapter's selected items to reference view model moviesSelected
        adapter.selectedItems =
            movieViewModel.moviesSelected.value as MutableList<MovieEntity?>

        // ListAdapter detects changes in items and updates the movies in RecyclerView
        movieViewModel.movies.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)

                // Check if the list of movies is empty and display text if empty
                if (it.isEmpty()) {
                    binding.emptyText.visibility = View.VISIBLE
                }
                else
                {
                    binding.emptyText.visibility = View.GONE
                }
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

        // Update movie when item changes
        adapter.movieEntityUpdate.observe(viewLifecycleOwner, Observer {
            it?.let {
                movieViewModel.updateMovie(it)
            }
        })

        // Add or remove selected item to selected in viewModel
        adapter.selectedItem.observe(viewLifecycleOwner, Observer {
            it?.let {
                movieViewModel.addOrRemoveSelected(it)
            }
        })

        /*
        If no movies are selected, hide options menu and change title to app name
        Else there are movies selected, show options menu and change title to amount selected
         */
        movieViewModel.moviesSelected.observe(viewLifecycleOwner, Observer {
            it?.let {
                checkSelected(it)
            }
        })

        // Set lifecycle owner to observe LiveData updates
        binding.lifecycleOwner = this

        return binding.root
    }

    // Check selected list to determine if in select mode or not
    private fun checkSelected(selected: MutableList<MovieEntity>?) {
        if (selected.isNullOrEmpty())
        {
            setHasOptionsMenu(false)

            // Hide clear icon next to title
            (activity as AppCompatActivity?)!!.supportActionBar?.
            setDisplayHomeAsUpEnabled(false)

            activity?.title = resources.getString(R.string.app_name)

            adapter.selectedMode = false
        }
        else
        {
            setHasOptionsMenu(true)

            // Show clear icon next to title
            (activity as AppCompatActivity?)!!.supportActionBar?.
            setDisplayHomeAsUpEnabled(true)

            activity?.title = "${selected.size} Selected"

            adapter.selectedMode = true
        }
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
                movieViewModel.movies.value?.forEach {
                    if (!movieViewModel.moviesSelected.value!!.contains(it)) {
                        movieViewModel.moviesSelected.value?.add(it)
                    }
                }
                movieViewModel.moviesSelected = movieViewModel.moviesSelected
                adapter.notifyDataSetChanged()
            }
            android.R.id.home -> {
                movieViewModel.resetMoviesSelected()
                adapter.notifyDataSetChanged()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Check for selected items onResume
    override fun onResume() {
        super.onResume()
        checkSelected(movieViewModel.moviesSelected.value)
    }

    // Delete selected movie items
    private fun deleteSelected () {
        val list: MutableList<MovieEntity> = ArrayList()
        list.addAll(movieViewModel.moviesSelected.value!!)
        movieViewModel.deleteMovies(list)

        // Clear the list of selected movies
        movieViewModel.resetMoviesSelected()

        // Reset title and hide options menu
        setHasOptionsMenu(false)
        activity?.setTitle(R.string.app_name)
    }

}