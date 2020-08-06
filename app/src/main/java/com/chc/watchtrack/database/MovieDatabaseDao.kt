package com.chc.watchtrack.database

import androidx.lifecycle.LiveData
import androidx.room.*

// Data access object
@Dao
interface MovieDatabaseDao
{
    // Insert movie
    @Insert
    fun insert(movie: MovieEntity)

    // Update movie
    @Update
    fun update(movie: MovieEntity)

    // Delete movie
    @Delete
    fun delete(movie: MovieEntity)

    // Get an item from the table, return nullable for empty cases
    @Query("SELECT * from movie_table WHERE movieId = :key")
    fun get(key: Long): MovieEntity?

    // Return the first element of list where movieId is in descending order
    @Query("SELECT * FROM movie_table ORDER BY movieId DESC LIMIT 1")
    fun getMovie(): MovieEntity?

    // Return all columns from the table in descending order of last_updated
    @Query("SELECT * FROM movie_table ORDER BY last_updated DESC")
    fun getAllMovies(): LiveData<List<MovieEntity>>
}
