package com.chc.watchtrack.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_table")
data class MovieEntity (
    @PrimaryKey(autoGenerate = true)
    var movieId: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String = "Title",

    @ColumnInfo(name = "hours")
    var hours: Int = 0,

    @ColumnInfo(name = "minutes")
    var minutes: Int = 0,

    @ColumnInfo(name = "seconds")
    var seconds: Int = 0
)