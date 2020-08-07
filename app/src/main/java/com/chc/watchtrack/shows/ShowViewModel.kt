package com.chc.watchtrack.shows

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chc.watchtrack.R
import com.chc.watchtrack.database.ShowEntity
import com.chc.watchtrack.database.ShowDatabaseDao
import kotlinx.coroutines.*
import java.util.*

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

    // When changed, ShowsFragment will enable actions to do something with the selected shows
    private var _showsSelected = MutableLiveData<MutableList<ShowEntity>?>()
    var showsSelected: LiveData<MutableList<ShowEntity>?>
        get() = _showsSelected
        set(value) {
            _showsSelected.value = value.value
        }

    // Clear _showsSelected list
    fun resetShowsSelected() {
        _showsSelected.value!!.clear()

        // Update shows selected value
        _showsSelected.value = _showsSelected.value
    }

    // Init showsSelected
    fun initShowsSelected() {
        _showsSelected.value = ArrayList()
    }

    /*
    Add item to selected if not already selected, if already selected, remove it
    Set background color of ViewHolder after selecting or deselecting
     */
    fun addOrRemoveSelected(selected: ShowListAdapter.ViewHolder) {
        if (_showsSelected.value!!.contains(selected.binding.show))
        {
            _showsSelected.value?.remove(selected.binding.show!!)
            selected.binding.cardView.setBackgroundColor(Color.WHITE)

        }
        else {
            _showsSelected.value?.add(selected.binding.show!!)
            selected.binding.cardView.setBackgroundColor(
                ContextCompat.getColor(selected.binding.root.context,
                    R.color.selectedItem
                ))
        }

        // Update shows selected value
        _showsSelected.value = _showsSelected.value
    }

    // Add new show coroutine
    private suspend fun insert(showEntity: ShowEntity) {
        withContext(Dispatchers.IO) {
            database.insert(showEntity)
        }
    }

    // Update show coroutine
    private suspend fun update(showEntity: ShowEntity) {
        withContext(Dispatchers.IO) {
            // Update last updated time for this item
            showEntity.lastUpdated = Date().toString()

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
