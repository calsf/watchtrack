package com.chc.watchtrack

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.chc.watchtrack.databinding.HomeFragmentBinding
import com.chc.watchtrack.movies.MoviesFragment
import com.chc.watchtrack.shows.ShowsFragment
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {
    private lateinit var viewPageAdapter: ViewPageAdapter
    private val tabTitles : Array<String> = arrayOf("Shows", "Movies")

    // Inflate fragment and setup tab layout, viewpager2, and the fab buttons to add show and movies
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Get reference to the binding object and inflate the fragment views
        val binding: HomeFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.home_fragment, container, false)

        /* Create view page adapter and add the fragments to switch between
        * Swiping left and right will switch/display fragments using the viewPageAdapter
        */
        viewPageAdapter = ViewPageAdapter(childFragmentManager, lifecycle)
        viewPageAdapter.addFragment(ShowsFragment())
        viewPageAdapter.addFragment(MoviesFragment())
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.adapter = viewPageAdapter

        // TEMP - navigate to addMovieFragment
        binding.addMovieButton.setOnClickListener{ Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_addMovieFragment)}

        // TEMP - navigate to addShowFragment
        binding.addShowButton.setOnClickListener{ Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_addShowFragment)}

        // Switch fragments using tab layout
        TabLayoutMediator(binding.tabBar, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()

        // Toggle fab when switching tabs
        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                toggleFab(position, binding)
            }
        })

        return binding.root
    }

    // Toggle fab button to add show or movie
    private fun toggleFab(position: Int, binding: HomeFragmentBinding)
    {
        when (position)
        {
            0 -> {
                binding.addShowButton.visibility = View.VISIBLE
                binding.addMovieButton.visibility = View.GONE
            }
            1 -> {
                binding.addMovieButton.visibility = View.VISIBLE
                binding.addShowButton.visibility = View.GONE
            }
            else -> {
                binding.addMovieButton.visibility = View.GONE
                binding.addShowButton.visibility = View.GONE
            }
        }
    }
}

