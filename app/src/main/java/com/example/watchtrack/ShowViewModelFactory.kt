package com.example.watchtrack

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.watchtrack.database.ShowDatabaseDao

// Provide data access object and context to the ViewModel
class ShowViewModelFactory (private val dataSource: ShowDatabaseDao,
                            private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowViewModel::class.java)) {
            return ShowViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}