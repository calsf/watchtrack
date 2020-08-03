package com.example.watchtrack

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.watchtrack.database.Show

@BindingAdapter("title")
fun TextView.setTitle(item: Show?) {
    item?.let {
        text = item.title
    }
}

@BindingAdapter("season")
fun TextView.setSeason(item: Show?) {
    item?.let {
        text = item.season.toString()
    }
}

@BindingAdapter("episode")
fun TextView.setEpisode(item: Show?) {
    item?.let {
        text = item.episode.toString()
    }
}

@BindingAdapter("hours")
fun TextView.setHours(item: Show?) {
    item?.let {
        text = item.hours.toString().padStart(2, '0')
    }
}

@BindingAdapter("minutes")
fun TextView.setMinutes(item: Show?) {
    item?.let {
        text = item.minutes.toString().padStart(2, '0')
    }
}

@BindingAdapter("seconds")
fun TextView.setSeconds(item: Show?) {
    item?.let {
        text = item.seconds.toString().padStart(2, '0')
    }
}

