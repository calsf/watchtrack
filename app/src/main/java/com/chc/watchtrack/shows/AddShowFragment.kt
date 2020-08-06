package com.chc.watchtrack.shows


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.chc.watchtrack.R
import com.chc.watchtrack.database.WatchDatabase
import com.chc.watchtrack.databinding.AddShowFragmentBinding


class AddShowFragment : Fragment() {
    private lateinit var failToast: Toast
    private lateinit var successToast: Toast

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Set action bar title
        activity?.title = "Adding Show"

        // Initialize toasts for successful and unsuccessful show add
        failToast = Toast.makeText(activity, R.string.fail_to_add_show, Toast.LENGTH_SHORT)
        successToast = Toast.makeText(activity, R.string.success_add_show, Toast.LENGTH_LONG)

        // Get reference to the binding object and inflate the fragment views
        val binding: AddShowFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.add_show_fragment, container, false)

        // Assign the EditTextTimes
        binding.hours.setEditTexts(binding.hours, binding.minutes, binding.seconds)
        binding.minutes.setEditTexts(binding.hours, binding.minutes, binding.seconds)
        binding.seconds.setEditTexts(binding.hours, binding.minutes, binding.seconds)

        // Create an instance of ShowViewModelFactory
        val application = requireNotNull(this.activity).application
        val dataSource = WatchDatabase.getInstance(application).showDatabaseDao
        val showViewModelFactory =
            ShowViewModelFactory(
                dataSource,
                application
            )

        // Get reference to ShowViewModel
        val showViewModel =
            ViewModelProvider(this, showViewModelFactory).get(ShowViewModel::class.java)

        // Add new show based on input, show failToast if exception is thrown
        binding.addButton.setOnClickListener {
            val title = binding.titleInput.text.toString()
            val season: Int
            val episode: Int
            val hours: Int
            val minutes: Int
            val seconds: Int
            try {
                season = binding.seasonNumber.text.toString().toInt()
                episode = binding.episodeNumber.text.toString().toInt()
                hours = binding.hours.text.toString().toInt()
                minutes = binding.minutes.text.toString().toInt()
                seconds = binding.seconds.text.toString().toInt()

                // Verify input before adding new show
                if (title.isEmpty() || season < 0 || episode < 0
                    || hours < 0 || minutes < 0 || seconds < 0)
                {
                    // Show fail toast
                    failToast.show()
                }
                else
                {
                    showViewModel.onAdd(title, season, episode, hours, minutes, seconds)

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


