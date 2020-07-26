package com.example.watchtrack

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.watchtrack.databinding.HomeFragmentBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var viewPageAdapter: ViewPageAdapter
    private val tabTitles : Array<String> = arrayOf("Shows", "Movies")

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

        // Switch fragments using tab layout
        TabLayoutMediator(binding.tabBar, binding.viewPager) { tab, position ->
        tab.text = tabTitles[position]
        binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()

        return binding.root
        }

}