package com.chc.watchtrack.movies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.chc.watchtrack.database.MovieDatabaseDao
import com.chc.watchtrack.database.MovieEntity
import kotlinx.coroutines.*

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

    // Add new movie coroutine
    private suspend fun insert(movieEntity: MovieEntity) {
        withContext(Dispatchers.IO) {
            database.insert(movieEntity)
        }
    }

    // Update movie coroutine
    private suspend fun update(movieEntity: MovieEntity) {
        withContext(Dispatchers.IO) {
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
