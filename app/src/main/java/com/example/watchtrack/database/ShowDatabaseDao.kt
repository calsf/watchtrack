package com.example.watchtrack.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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

    // Get an item from the table, return nullable for empty cases
    @Query ("SELECT * from show_table WHERE showId = :key")
    fun get(key: Long): Show?

    // Deletes all items in "show_table"
    @Query ("DELETE FROM show_table")
    fun clear()

    // Return the first element of list where showId is in descending order
    @Query ("SELECT * FROM show_table ORDER BY showId DESC LIMIT 1")
    fun getTonight(): Show?

    // Return all columns from the table in descending order
    @Query ("SELECT * FROM show_table ORDER BY showId DESC")
    fun getAllShows(): LiveData<List<Show>>
}
