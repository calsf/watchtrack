package com.example.watchtrack.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "show_table")
data class Show (
    @PrimaryKey(autoGenerate = true)
    var showId: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String = "Title",

    @ColumnInfo(name = "hours")
    var hours: Long = 0,

    @ColumnInfo(name = "minutes")
    var minutes: Long = 0,

    @ColumnInfo(name = "seconds")
    var seconds: Long = 0,

    @ColumnInfo(name = "season")
    var season: Long = -1,

    @ColumnInfo(name = "episode")
    var episode: Long = -1
)