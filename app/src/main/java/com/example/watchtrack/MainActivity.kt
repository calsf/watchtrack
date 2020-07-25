package com.example.watchtrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {
    private lateinit var viewPageAdapter: ViewPageAdapter
    val tabTitles : Array<String> = arrayOf("Shows", "Movies")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<TabLayout>(R.id.tab_bar)
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager)

        /* Create view page adapter and add the fragments to switch between
         * Swiping left and right will switch/display fragments using the viewPageAdapter
         */
        viewPageAdapter = ViewPageAdapter(supportFragmentManager, lifecycle)
        viewPageAdapter.addFragment(ShowsFragment())
        viewPageAdapter.addFragment(MoviesFragment())
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager2.adapter = viewPageAdapter

        // Switch fragments using tab layout
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = tabTitles[position]
            viewPager2.setCurrentItem(tab.position, true)
        }.attach()

    }
}

