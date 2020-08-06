package com.chc.watchtrack.movies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.chc.watchtrack.R
import com.chc.watchtrack.database.WatchDatabase
import com.chc.watchtrack.databinding.AddMovieFragmentBinding
import com.chc.watchtrack.shows.ShowViewModel
import com.chc.watchtrack.shows.ShowViewModelFactory

class AddMovieFragment : Fragment() {
    private lateinit var failToast: Toast
    private lateinit var successToast: Toast

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Set action bar title
        activity?.title = "Adding Movie"

        // Initialize toasts for successful and unsuccessful movie add
        failToast = Toast.makeText(activity, R.string.fail_to_add_movie, Toast.LENGTH_SHORT)
        successToast = Toast.makeText(activity, R.string.success_add_movie, Toast.LENGTH_LONG)

        // Get reference to the binding object and inflate the fragment views
        val binding: AddMovieFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.add_movie_fragment, container, false)

        // Assign the EditTextTimes
        binding.hours.setEditTexts(binding.hours, binding.minutes, binding.seconds)
        binding.minutes.setEditTexts(binding.hours, binding.minutes, binding.seconds)
        binding.seconds.setEditTexts(binding.hours, binding.minutes, binding.seconds)

        // Create an instance of MovieViewModelFactory
        val application = requireNotNull(this.activity).application
        val dataSource = WatchDatabase.getInstance(application).movieDatabaseDao
        val movieViewModelFactory =
            MovieViewModelFactory(
                dataSource,
                application
            )

        // Get reference to MovieViewModel
        val movieViewModel =
            ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        // Add new movie based on input, show failToast if exception is thrown
        binding.addButton.setOnClickListener {
            val title = binding.titleInput.text.toString()
            val hours: Int
            val minutes: Int
            val seconds: Int
            try {
                hours = binding.hours.text.toString().toInt()
                minutes = binding.minutes.text.toString().toInt()
                seconds = binding.seconds.text.toString().toInt()

                // Verify input before adding new movie
                if (title.isEmpty() || hours < 0 || minutes < 0 || seconds < 0)
                {
                    // Show fail toast
                    failToast.show()
                }
                else
                {
                    movieViewModel.onAdd(title, hours, minutes, seconds)

                    // Show successfully added toast and go back
                    successToast.show()
                    Navigation.findNavController(it).navigateUp()
                }
            }
            catch(nfe: NumberFormatException)
            {
                failToast.show()
            }
        }

        return binding.root
    }
}