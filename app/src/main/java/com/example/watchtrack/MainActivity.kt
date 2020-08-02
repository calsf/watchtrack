package com.example.watchtrack

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.watchtrack.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Init inputMethodManager
        inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Get reference to the binding object, lock drawer
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // Setup action bar with navigation
        navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(binding.navView, navController)

        // Remove action bar shadow
        supportActionBar?.elevation = 0f
    }

    // Navigate up
    override fun onSupportNavigateUp(): Boolean {
        // Hide input
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    // Hide input if tapping outside of a focused EditText, does not clear focus
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val view: View? = currentFocus

            // Check if focused view is an EditText
            if (view is EditText) {
                // If tap position is outside of the focused EditText, clear focus and hide input
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    // If it is a EditTextTime, reset color
                    if (view is EditTextTime)
                    {
                        view.resetTextColor()
                    }
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}

