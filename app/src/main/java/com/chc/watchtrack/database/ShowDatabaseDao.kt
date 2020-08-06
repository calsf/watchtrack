package com.chc.watchtrack.database

import androidx.lifecycle.LiveData
import androidx.room.*

// Data access object
@Dao
interface ShowDatabaseDao
{
    // Insert show
    @Insert
    fun insert(showEntity: ShowEntity)

    // Update show
    @Update
    fun update(showEntity: ShowEntity)

    // Delete show
    @Delete
    fun delete(showEntity: ShowEntity)

    // Get an item from the table, return nullable for empty cases
    @Query ("SELECT * from show_table WHERE showId = :key")
    fun get(key: Long): ShowEntity?

    // Return the first element of list where showId is in descending order
    @Query ("SELECT * FROM show_table ORDER BY showId DESC LIMIT 1")
    fun getShow(): ShowEntity?

    // Return all columns from the table in descending order of last_updated
    @Query ("SELECT * FROM show_table ORDER BY last_updated DESC")
    fun getAllShows(): LiveData<List<ShowEntity>>
}
