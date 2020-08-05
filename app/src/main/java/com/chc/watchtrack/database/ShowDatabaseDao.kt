package com.chc.watchtrack.database

import androidx.lifecycle.LiveData
import androidx.room.*

// Data access object
@Dao
interface ShowDatabaseDao
{
    // Insert show
    @Insert
    fun insert(show: Show)

    // Update show
    @Update
    fun update(show: Show)

    // Delete show
    @Delete
    fun delete(show: Show)

    // Get an item from the table, return nullable for empty cases
    @Query ("SELECT * from show_table WHERE showId = :key")
    fun get(key: Long): Show?

    // Return the first element of list where showId is in descending order
    @Query ("SELECT * FROM show_table ORDER BY showId DESC LIMIT 1")
    fun getShow(): Show?

    // Return all columns from the table in descending order
    @Query ("SELECT * FROM show_table ORDER BY showId DESC")
    fun getAllShows(): LiveData<List<Show>>
}
