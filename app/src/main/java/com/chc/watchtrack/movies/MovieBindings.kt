package com.chc.watchtrack.movies

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chc.watchtrack.database.MovieEntity

/*
Bind and display Movie values in movie_item.xml text views
 */

@BindingAdapter("title")
fun TextView.setTitle(item: MovieEntity?) {
    item?.let {
        text = item.title
    }
}

@BindingAdapter("hours")
fun TextView.setHours(item: MovieEntity?) {
    item?.let {
        text = item.hours.toString().padStart(2, '0')
    }
}

@BindingAdapter("minutes")
fun TextView.setMinutes(item: MovieEntity?) {
    item?.let {
        text = item.minutes.toString().padStart(2, '0')
    }
}

@BindingAdapter("seconds")
fun TextView.setSeconds(item: MovieEntity?) {
    item?.let {
        text = item.seconds.toString().padStart(2, '0')
    }
}