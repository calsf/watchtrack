package com.chc.watchtrack

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

// Adapter for displaying fragments with ViewPager2 and TabLayout
class ViewPageAdapter(fm : FragmentManager, lifecycle : Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    // List of fragments to switch between based on tab selected
    private val _fragmentList: MutableList<Fragment> = ArrayList()

    // Add fragment to fragment list
    fun addFragment(fragment: Fragment) {
        _fragmentList.add(fragment)
    }

    // Return total number of fragments
    override fun getItemCount(): Int {
        return _fragmentList.size
    }

    // Return corresponding fragment with the tab position
    override fun createFragment(position: Int): Fragment {
        return _fragmentList[position]
    }

}