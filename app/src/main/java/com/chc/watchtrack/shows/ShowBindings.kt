package com.chc.watchtrack.shows

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chc.watchtrack.database.ShowEntity
/*
Bind and display Show values in show_item.xml text views
 */

@BindingAdapter("title")
fun TextView.setTitle(item: ShowEntity?) {
    item?.let {
        text = item.title
    }
}

@BindingAdapter("season")
fun TextView.setSeason(item: ShowEntity?) {
    item?.let {
        text = item.season.toString()
    }
}

@BindingAdapter("episode")
fun TextView.setEpisode(item: ShowEntity?) {
    item?.let {
        text = item.episode.toString()
    }
}

@BindingAdapter("hours")
fun TextView.setHours(item: ShowEntity?) {
    item?.let {
        text = item.hours.toString().padStart(2, '0')
    }
}

@BindingAdapter("minutes")
fun TextView.setMinutes(item: ShowEntity?) {
    item?.let {
        text = item.minutes.toString().padStart(2, '0')
    }
}

@BindingAdapter("seconds")
fun TextView.setSeconds(item: ShowEntity?) {
    item?.let {
        text = item.seconds.toString().padStart(2, '0')
    }
}

