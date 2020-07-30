package com.example.watchtrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.watchtrack.database.Show
import com.example.watchtrack.database.ShowDatabaseDao
import kotlinx.coroutines.*

class ShowViewModel (
    private val dataSource: ShowDatabaseDao, application: Application
) : AndroidViewModel(application) {
    // Data access object reference
    val database = dataSource

    // Create Job to allow cancelling of all coroutines started by this ViewModel
    private var viewModelJob = Job()

    // Use main thread for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // Get list of shows as LiveData so can be observed and updated
    val shows = database.getAllShows()

    // Add new show coroutine
    private suspend fun insert(show: Show) {
        withContext(Dispatchers.IO) {
            database.insert(show)
        }
    }

    // Update show coroutine
    private suspend fun update(show: Show) {
        withContext(Dispatchers.IO) {
            database.update(show)
        }
    }

    // Clear table coroutine
    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    // For onClick, add new show with given data
    fun onAdd(title: String) {
        uiScope.launch {
            // Create new show and insert into database
            val newShow = Show()
            newShow.title = title
            insert(newShow)

        }
    }

    // For onClick, clear table
    fun onClear() {
        uiScope.launch {
            clear()
        }
    }

    // When ViewModel dismantled, cancel all coroutines to prevent wasting memory and resources
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
