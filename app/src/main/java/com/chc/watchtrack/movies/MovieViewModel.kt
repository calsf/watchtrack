package com.chc.watchtrack.movies

import android.app.Application
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chc.watchtrack.R
import com.chc.watchtrack.database.MovieDatabaseDao
import com.chc.watchtrack.database.MovieEntity
import kotlinx.coroutines.*
import java.util.*

class MovieViewModel (
    dataSource: MovieDatabaseDao, application: Application
) : AndroidViewModel(application) {
    // Data access object reference
    val database = dataSource

    // Create Job to allow cancelling of all coroutines started by this ViewModel
    private var viewModelJob = Job()

    // Use main thread for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // Get list of movies as LiveData so can be observed and updated
    val movies = database.getAllMovies()

    // When changed, MoviesFragment will enable actions to do something with the selected movies
    private var _moviesSelected = MutableLiveData<MutableList<MovieEntity>?>()
    var moviesSelected: LiveData<MutableList<MovieEntity>?>
        get() = _moviesSelected
        set(value) {
            _moviesSelected.value = value.value
        }

    // Clear _moviesSelected list
    fun resetMoviesSelected() {
        _moviesSelected.value!!.clear()

        // Update movies selected value
        _moviesSelected.value = _moviesSelected.value
    }

    // Init moviesSelected
    fun initMoviesSelected() {
        _moviesSelected.value = ArrayList()
    }

    /*
    Add item to selected if not already selected, if already selected, remove it
    Set background color of ViewHolder after selecting or deselecting
     */
    fun addOrRemoveSelected(selected: MovieListAdapter.ViewHolder) {
        if (_moviesSelected.value!!.contains(selected.binding.movie))
        {
            _moviesSelected.value?.remove(selected.binding.movie!!)
            selected.binding.cardView.setBackgroundColor(Color.WHITE)

        }
        else {
            _moviesSelected.value?.add(selected.binding.movie!!)
            selected.binding.cardView.setBackgroundColor(
                ContextCompat.getColor(selected.binding.root.context,
                    R.color.selectedItem
                ))
        }

        // Update moviesSelected value
        _moviesSelected.value = _moviesSelected.value
    }


    // Add new movie coroutine
    private suspend fun insert(movieEntity: MovieEntity) {
        withContext(Dispatchers.IO) {
            database.insert(movieEntity)
        }
    }

    // Update movie coroutine
    private suspend fun update(movieEntity: MovieEntity) {
        withContext(Dispatchers.IO) {
            // Update last updated time for this item
            movieEntity.lastUpdated = Date().toString()

            database.update(movieEntity)
        }
    }

    // Delete all movies in given list
    private suspend fun delete(movieEntities: List<MovieEntity>?){
        withContext(Dispatchers.IO) {
            movieEntities?.forEach {
                database.delete(it)
            }
        }
    }

    // For onClick, add new movie with given data
    fun onAdd(title: String, hours: Int, minutes: Int, seconds: Int) {
        val newMovie = MovieEntity()
        uiScope.launch {
            // Create new movie and insert into database
            newMovie.title = title
            newMovie.hours = hours
            newMovie.minutes = minutes
            newMovie.seconds = seconds
            insert(newMovie)
        }
    }

    // Update movie in MoviesFragment when update status is true
    fun updateMovie(movieEntity: MovieEntity)
    {
        uiScope.launch {
            update(movieEntity)
        }
    }

    // Delete list of shows
    fun deleteMovies(movieEntities: List<MovieEntity>?){
        uiScope.launch {
            delete(movieEntities)
        }
    }

    // When ViewModel dismantled, cancel all coroutines to prevent wasting memory and resources
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
