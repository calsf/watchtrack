package com.chc.watchtrack.shows

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.chc.watchtrack.database.ShowEntity
import com.chc.watchtrack.database.ShowDatabaseDao
import kotlinx.coroutines.*

class ShowViewModel (
    dataSource: ShowDatabaseDao, application: Application
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
    private suspend fun insert(showEntity: ShowEntity) {
        withContext(Dispatchers.IO) {
            database.insert(showEntity)
        }
    }

    // Update show coroutine
    private suspend fun update(showEntity: ShowEntity) {
        withContext(Dispatchers.IO) {
            database.update(showEntity)
        }
    }

    // Delete all shows in given list
    private suspend fun delete(showEntities: List<ShowEntity>?){
        withContext(Dispatchers.IO) {
            showEntities?.forEach {
                database.delete(it)
            }
        }
    }

    // For onClick, add new show with given data
    fun onAdd(title: String, season: Int, episode: Int, hours: Int, minutes: Int, seconds: Int) {
        val newShow = ShowEntity()
        uiScope.launch {
            // Create new show and insert into database
            newShow.title = title
            newShow.season = season
            newShow.episode = episode
            newShow.hours = hours
            newShow.minutes = minutes
            newShow.seconds = seconds
            insert(newShow)
        }
    }

    // Update show in ShowsFragment when update status is true
    fun updateShow(showEntity: ShowEntity)
    {
        uiScope.launch {
            update(showEntity)
        }
    }

    // Delete list of shows
    fun deleteShows(showEntities: List<ShowEntity>?){
        uiScope.launch {
            delete(showEntities)
        }
    }

    // When ViewModel dismantled, cancel all coroutines to prevent wasting memory and resources
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
